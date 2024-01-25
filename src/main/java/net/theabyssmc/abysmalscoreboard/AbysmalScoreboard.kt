package net.theabyssmc.abysmalscoreboard

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot
import net.kyori.adventure.text.Component
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class AbysmalScoreboard : JavaPlugin(), Listener {
    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this)
        getCommand("scoreboard")!!.setExecutor(ScoreboardCommand)
        logger.info("AbysmalScoreboard has loaded")
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        AbysmalScoreboardManager.createScoreboard(event.player)
    }

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        AbysmalScoreboardManager.updateDeaths(player)
        Bukkit.broadcast(Component.text("Player $player died"))
    }

    @EventHandler
    fun onPlayerKill(event: PlayerDeathEvent) {
        val killer = event.entity.killer
        val victim = event.entity

        if (killer != null) {
            AbysmalScoreboardManager.updatePvpKills(killer)
            logger.info("Player $victim was killed by $killer")
            Bukkit.broadcast(Component.text("Player ${victim.name} was killed by $killer"))
        }
    }

    companion object {
        fun getPlugin(): AbysmalScoreboard {
            return getPlugin(AbysmalScoreboard::class.java)
        }
    }

    override fun onDisable() {
        logger.info("AbysmalScoreboard has unloaded")
    }
}

object ScoreboardCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender is Player) {
            AbysmalScoreboardManager.toggleScoreboard(sender)
        }
        return true
    }
}

object AbysmalScoreboardManager {

    private val hiddenObjectives = mutableSetOf<String>()
    private val deathsMap = mutableMapOf<Player, Int>()
    private val pvpKillsMap = mutableMapOf<Player, Int>()

    private val gson = Gson()
    private val dataFile = File("plugins/AbysmalScoreboard/player_data.json")

    init {
        loadData()
    }

    private fun loadData() {
        if (!dataFile.exists()) {
            dataFile.parentFile.mkdirs()
            saveData()
            return
        }

        FileReader(dataFile).use { reader ->
            val type = object : TypeToken<Map<String, Pair<Int, Int>>>() {}.type
            val data: Map<String, Pair<Int, Int>> = gson.fromJson(reader, type)

            deathsMap.clear()
            pvpKillsMap.clear()

            data.forEach { (playerName, scores) ->
                val player = Bukkit.getPlayerExact(playerName)
                if (player != null) {
                    deathsMap[player] = scores.first
                    pvpKillsMap[player] = scores.second
                }
            }
        }
    }

    private fun saveData() {
        val data = deathsMap.map { (player, deaths) -> player.name to deaths }.toMap()
        val json = gson.toJson(data)

        FileWriter(dataFile).use { writer ->
            writer.write(json)
        }
    }

    fun createScoreboard(player: Player) {
        val scoreboard: Scoreboard = Bukkit.getScoreboardManager().newScoreboard
        val objective: Objective = scoreboard.registerNewObjective("killDeathCounter", Criteria.DUMMY, Component.text("The Abyss SMP"))

        objective.displaySlot = DisplaySlot.SIDEBAR
        player.scoreboard = scoreboard
    }

    fun toggleScoreboard(player: Player) {
        val scoreboard = player.scoreboard
        val objective = scoreboard.getObjective("killDeathCounter")

        if (objective != null) {
            if (hiddenObjectives.contains(player.name)) {
                objective.displaySlot = DisplaySlot.SIDEBAR
                hiddenObjectives.remove(player.name)
            } else {
                objective.displaySlot = null
                hiddenObjectives.add(player.name)
            }
        }
    }

    fun updateDeaths(player: Player) {
        deathsMap[player] = deathsMap.getOrDefault(player, 0) + 1
        updateScore(player, "deaths", deathsMap[player] ?: 0)
        saveData()
    }

    fun updatePvpKills(player: Player) {
        pvpKillsMap[player] = pvpKillsMap.getOrDefault(player, 0) + 1
        updateScore(player, "pvpKills", pvpKillsMap[player] ?: 0)
        saveData()
    }

    private fun updateScore(player: Player, objectiveName: String, score: Int) {
        val scoreboard: Scoreboard = player.scoreboard
        val objective: Objective? = scoreboard.getObjective(objectiveName)

        if (objective != null) {
            val scoreObj = objective.getScore(player.name)
            scoreObj.score = score
        }
    }
}
