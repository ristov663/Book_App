import dev.d1s.ktor.liquibase.plugin.LiquibaseMigrations
import io.ktor.server.application.Application
import io.ktor.server.application.install
import java.sql.Connection

fun Application.ConfigureLiquibase(dbConnection: Connection) {
    install(LiquibaseMigrations) {
        connection = dbConnection
        changeLogPath = "db/changelog/master.xml"
    }
}
