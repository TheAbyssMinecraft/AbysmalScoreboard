import net.theabyssmc.abysmalscoreboard.AbysmalScoreboardManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        AbysmalScoreboardManager.createScoreboard(event.player)
    }
}