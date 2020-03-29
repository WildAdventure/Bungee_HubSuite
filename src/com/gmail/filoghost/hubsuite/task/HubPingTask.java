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
package com.gmail.filoghost.hubsuite.task;

import java.util.Set;

import com.gmail.filoghost.hubsuite.HubSuite;
import com.gmail.filoghost.hubsuite.util.Pinger;
import com.google.common.collect.Sets;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class HubPingTask implements Runnable {
	
	private HubSuite plugin;
	private Set<String> offlineHubs = Sets.newConcurrentHashSet();
	
	public HubPingTask(HubSuite plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		for (final String serverName : plugin.getSettings().hubs) {
			final ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(serverName);
			
			if (serverInfo == null) {
				offlineHubs.remove(serverName); // The plugin will handle this

			} else {
				// Ping it!
				if (Pinger.isOnline(serverInfo.getAddress(), plugin.getSettings().ping_timeout)) {
					offlineHubs.remove(serverName); // Pinged, remove from offline hubs
				} else {
					plugin.getLogger().info("Could not reach hub: " + serverName);
					offlineHubs.add(serverName); // Mark it as offline
				}
			}
		}
	}
	
	public boolean isOffline(String server) {
		return offlineHubs.contains(server);
	}
}
