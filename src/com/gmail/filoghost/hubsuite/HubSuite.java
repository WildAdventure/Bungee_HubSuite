/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gmail.filoghost.hubsuite;

import java.util.concurrent.TimeUnit;

import com.gmail.filoghost.hubsuite.command.HubCommand;
import com.gmail.filoghost.hubsuite.command.HubSuiteCommand;
import com.gmail.filoghost.hubsuite.listener.HubBalancerListener;
import com.gmail.filoghost.hubsuite.listener.KickListener;
import com.gmail.filoghost.hubsuite.task.HubPingTask;

import lombok.Getter;
import net.cubespace.Yamler.Config.InvalidConfigurationException;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class HubSuite extends Plugin {

	@Getter private Settings settings;
	@Getter private boolean debug;
	
	private HubPingTask pingTask;
	private ScheduledTask scheduledPingTask;

	@Getter private HubBalancerListener hubBalancer;

	@Override
	public void onEnable() {
		settings = new Settings(this);
		
		try {
			load();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		getProxy().getPluginManager().registerListener(this, hubBalancer = new HubBalancerListener(this));
		getProxy().getPluginManager().registerListener(this, new KickListener(this));
		
		getProxy().getPluginManager().registerCommand(this, new HubCommand(this, "hub"));
		getProxy().getPluginManager().registerCommand(this, new HubSuiteCommand(this, "hubsuite"));
	}
	
	
	public boolean toggleDebug() {
		debug = !debug;
		return debug;
	}

	
	public void load() throws InvalidConfigurationException {
		settings.init();
		
		if (scheduledPingTask != null) {
			scheduledPingTask.cancel();
		}
		
		if (settings.ping_interval > 0) {
			pingTask = new HubPingTask(this);
			scheduledPingTask = getProxy().getScheduler().schedule(this, pingTask, 0, Math.max(settings.ping_interval, 100), TimeUnit.MILLISECONDS);
		}
	}
	
	
	public ServerInfo getBestHub() {
		ServerInfo bestHub = null;
		int bestHubPlayers = 0;
		
		for (String hubName : settings.hubs) {
			
			if (pingTask.isOffline(hubName)) {
				continue; // Make sure it's not offline, else skip it
			}
			
			ServerInfo currentHub = getProxy().getServerInfo(hubName);
			if (currentHub == null) {
				continue; // Make sure it exists
			}
				
			if (currentHub.getPlayers().size() < settings.overflowThreshold) {
				return currentHub; // We found a hub with less players than the threshold
			}
				
			int currentHubPlayers = currentHub.getPlayers().size();
			
			// Select the hub with less players, if all of them exceed the threshold
			if (bestHub == null || currentHubPlayers < bestHubPlayers) {
				bestHub = currentHub;
				bestHubPlayers = currentHubPlayers;
			}
		}
		
		return bestHub;
	}

	
	public boolean isHub(ServerInfo server) {
		return settings.hubs.contains(server.getName());
	}
	
}
