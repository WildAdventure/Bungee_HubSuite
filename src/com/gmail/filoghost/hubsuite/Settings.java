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

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.cubespace.Yamler.Config.Comment;
import net.cubespace.Yamler.Config.Comments;
import net.cubespace.Yamler.Config.Config;
import net.md_5.bungee.api.plugin.Plugin;

public class Settings extends Config {
	
	
	@Comment("Lista dei server hub, sensibile alle maiuscole")
	public List<String> hubs = Arrays.asList("hub");
	
	
	@Comments({"Limite oltre il quale i giocatori vengono mandati all'hub più vuota.",
			   "Impostalo a 0 per bilanciare perfettamente le hub."})
	public int overflowThreshold = 100;
	
	
	@Comments({"Ogni quanto controllare che le hub siano online, in millisecondi.",
			   "Imposta a 0 per disattivare."})
	public int ping_interval = 3000;
	
	
	@Comments({"Il timeout per il ping in millisecondi.",
	   		   "Non impostare a valori superiori a 5000."})
	public int ping_timeout = 1000;
	
	
	@Comment("Scegli se mandare i giocatori kickati per afk all'hub.")
	public boolean hubKick_afk = true;
	
	
	@Comment("Se il giocatore viene kickato da uno di questi server, viene invece mandato all'hub.")
	public List<String> hubKick_servers = Arrays.asList("server-1", "server-2");

	
	@Comment("Se il messaggio di kick contiene questa stringa, il giocatore viene invece mandato all'hub.")
	public String hubKick_specialString = "§0§0§0";

	
	
	public Settings(Plugin plugin) {
		CONFIG_HEADER = new String[] {"Configurazione HubSuite"};
		CONFIG_FILE = new File(plugin.getDataFolder(), "config.yml");
	}

	
}
