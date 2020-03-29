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

import com.gmail.filoghost.hubsuite.HubSuite;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import wild.api.chat.Chat;

public class KickListener implements Listener {
	
	private HubSuite plugin;
	
	public KickListener(HubSuite plugin) {
		this.plugin = plugin;
	}

	@EventHandler (priority = EventPriority.HIGHEST)
    public void onServerKickEvent(ServerKickEvent event) {
		
		if (event.isCancelled()) {
			return;
		}
		
		String kickReason = extractText(event.getKickReasonComponent());
        ServerInfo kickedFrom = event.getKickedFrom();
        
        if (plugin.isDebug()) {
        	plugin.getLogger().info("Kick reason for " + event.getPlayer().getName() + ": " + kickReason.replace("§", "&"));
        	plugin.getLogger().info("Trigger string: " + plugin.getSettings().hubKick_specialString.replace("§", "&"));
        }

        if (plugin.getSettings().hubKick_afk && isAfkKick(kickReason)) {
        	
        	if (plugin.isHub(kickedFrom)) {
        		// Kick definitivo
	        	event.setKickReasonComponent(Chat.makeArray("Sei stato inattivo per troppo tempo.", ChatColor.WHITE));
        	} else {
        		ServerInfo bestHub = plugin.getBestHub();
    			if (bestHub != null) {
	        		event.setCancelled(true);
	            	event.setCancelServer(bestHub);
	            	plugin.getHubBalancer().ignoreNextEvent(event.getPlayer());
	            	Chat.tell(event.getPlayer(), "Sei stato mandato al server principale perché sei rimasto inattivo per troppo tempo.", ChatColor.RED);
    			}
        	}

        } else if (!plugin.isHub(kickedFrom) && (plugin.getSettings().hubKick_servers.contains(kickedFrom.getName()) || kickReason.contains(plugin.getSettings().hubKick_specialString))) {
        	ServerInfo bestHub = plugin.getBestHub();
			if (bestHub != null) {
				event.setCancelled(true);
	        	event.setCancelServer(bestHub);
	        	plugin.getHubBalancer().ignoreNextEvent(event.getPlayer());
	        	Chat.tell(event.getPlayer(), ">> Sei stato mandato al server principale:", ChatColor.RED);
	        	Chat.tell(event.getPlayer(), ">> " + ChatColor.stripColor(kickReason).replace(plugin.getSettings().hubKick_specialString, ""), ChatColor.RED);
			}
        }
    }
	
	private boolean isAfkKick(String kickReason) {
		return kickReason.contains("You have been idle for too long!") || kickReason.contains("Sei stato troppo tempo AFK.");
	}

	private String extractText(BaseComponent[] message) {
		StringBuilder text = new StringBuilder();
		for (BaseComponent base : message) {
			if (base instanceof TextComponent) {
				text.append(((TextComponent) base).getText());
			}
		}
		return text.toString();
	}
    
}
