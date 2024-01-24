package net.theabyssmc.abysmalscoreboard

import PlayerJoinListener
import ScoreboardCommand
import org.bukkit.plugin.java.JavaPlugin

class AbysmalScoreboard : JavaPlugin() {
    override fun onEnable() {
        // Plugin startup logic
        logger.info("AbysmalScoreboard has loaded!")

        getCommand("sb")?.setExecutor(ScoreboardCommand())

        server.pluginManager.registerEvents(PlayerJoinListener(), this)
        logger.info("PlayerJoinListener loaded!")
        server.pluginManager.registerEvents(PlayerDeathListener(), this)
        logger.info("PlayerDeathListener loaded!")
        server.pluginManager.registerEvents(PlayerKillListener(), this)
        logger.info("PlayerKillListener loaded!")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        logger.info("AbysmalScoreboard has unloaded!")
    }
}
