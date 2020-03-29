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
package com.gmail.filoghost.hubsuite.listener;

import java.util.Set;

import com.gmail.filoghost.hubsuite.HubSuite;
import com.google.common.collect.Sets;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class HubBalancerListener implements Listener {
	
	private Set<ProxiedPlayer> ignoreNextEvent = Sets.newConcurrentHashSet();
	private HubSuite plugin;
	
	public HubBalancerListener(HubSuite plugin) {
		this.plugin = plugin;
	}
	
	public void ignoreNextEvent(ProxiedPlayer player) {
		ignoreNextEvent.add(player);
	}
	
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onConnect(ServerConnectEvent event) {

		if (ignoreNextEvent.remove(event.getPlayer())) {
			if (plugin.isDebug()) {
				plugin.getLogger().info("Ignored connect event for " + event.getPlayer().getName());
			}
			return;
		}
		
		if (event.isCancelled()) {
			return;
		}
		
		ServerInfo fromServer = event.getPlayer().getServer() != null ? event.getPlayer().getServer().getInfo() : null;
		ServerInfo toServer = event.getTarget();
		if (plugin.isDebug()) {
			plugin.getLogger().info(event.getPlayer().getName() + " is connecting to " + toServer.getName() + " from " + (fromServer != null ? fromServer.getName() : "-"));
		}
		
		// Se ci si connette a un hub per la prima volta o da un server che non è hub
		if (plugin.isHub(toServer) && (fromServer == null || !plugin.isHub(fromServer))) {
			ServerInfo bestHub = plugin.getBestHub();
			if (bestHub != null) {
				
				event.setTarget(bestHub);
			}
		}
	}

}
