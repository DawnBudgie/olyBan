package com.olympuspvp.ban;

import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;


public class LoginListener implements Listener{

	final olyBan ban;
	
	protected LoginListener(olyBan ob){
		ban = ob;
		Bukkit.getPluginManager().registerEvents(this, ban);
	}
	
	@EventHandler
	public void onPlayerConnect(PlayerLoginEvent e){
		Player p = e.getPlayer();
		if(ban.isBanned(p.getName())){
			e.setResult(Result.KICK_OTHER);
			e.setKickMessage(ban.getBanReason(p.getName(), true));
			return;
		}
	}
	
	String failedLogin = null;
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent e){
		Player p = e.getPlayer();
		String ip = olyBan.strFromIP(p.getAddress().getAddress().getAddress());
		List<String> twoBans = ban.config.getStringList("BanLogs.IP.2");
		if(twoBans.contains(ip)){
			e.setJoinMessage(null);
			p.kickPlayer(ban.tag + "You have been banned on two accounts from this IP address.\nCurrent ip: " + ChatColor.DARK_RED + ip + "\n" + ChatColor.RED + "If you would like to dispute this ban, visit www.olympuspvp.com");
			failedLogin = p.getName();
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(failedLogin == null) return;
		if(p.getName().equals(failedLogin)){
			e.setQuitMessage(null);
			failedLogin = null;
		}
	}
	
}
