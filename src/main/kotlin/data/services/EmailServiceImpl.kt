package com.example.data.services

import com.example.domain.services.EmailService
import io.ktor.server.application.*
import org.simplejavamail.api.email.EmailPopulatingBuilder
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.email.EmailBuilder
import org.simplejavamail.mailer.MailerBuilder

class EmailServiceImpl(private val application: Application) : EmailService {

    private val mailer: Mailer by lazy {
        val host = application.environment.config.property("mail.host").getString()
        val port = application.environment.config.property("mail.port").getString().toInt()
        val username = application.environment.config.property("mail.username").getString()
        val password = application.environment.config.property("mail.password").getString()

        MailerBuilder
            .withSMTPServer(host, port, username, password)
            .withTransportStrategy(TransportStrategy.SMTP_TLS)
            .buildMailer()
    }

    private val senderEmail = application.environment.config.property("mail.username").getString()

    override fun sendVerificationEmail(email: String, code: String) {
        val emailContent = buildEmailContent(code)

        val emailBuilder: EmailPopulatingBuilder = EmailBuilder.startingBlank()
            .from("VoiceFlow", senderEmail)
            .to(email)
            .withSubject("Your Books Application Verification Code")
            .withHTMLText(emailContent)

        mailer.sendMail(emailBuilder.buildEmail())
        application.log.info("Email sent to $email with verification code")
    }

    private fun buildEmailContent(code: String): String {
        return """
        <html>
        <body>
            <h2>Book Application Verification Code</h2>
            <p>Your verification code is: <strong>$code</strong></p>
            <p>This code will expire in 10 minutes.</p>
        </body>
        </html>
        """.trimIndent()
    }
}
