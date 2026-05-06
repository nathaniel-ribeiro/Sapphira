import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands

class Sapphira : CliktCommand() {
    override fun run() = Unit
}

fun main(args: Array<String>) = Sapphira()
    .subcommands(Trainer(), Server())
    .main(args)