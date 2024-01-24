import net.theabyssmc.abysmalscoreboard.AbysmalScoreboardManager
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ScoreboardCommand : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender is Player) {
            AbysmalScoreboardManager.toggleScoreboard(sender)
        }
        return true
    }
}