package net.theabyssmc.abysmalscoreboard

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

class PlayerKillListener : Listener {

    @EventHandler
    fun onPlayerKill(event: PlayerDeathEvent) {
        val killer = event.entity.killer
        val victim = event.entity

        if (killer != null) {
            AbysmalScoreboardManager.updatePvpKills(killer)
        }
    }
}