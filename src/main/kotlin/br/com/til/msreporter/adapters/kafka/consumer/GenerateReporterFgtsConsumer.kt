package br.com.til.msreporter.adapters.kafka.consumer

import br.com.til.msreporter.adapters.kafka.dtos.PayRollReportDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant

@Service
class GenerateReportFgtsConsumer () {

    @KafkaListener(topics = ["reportTopic"], groupId = "reportGroupId")
    fun increaseSalary(record: ConsumerRecord<String, String>) {

        println("PayRoll Report Json: ${record.value()}")
        val reportDTO: PayRollReportDTO = jacksonObjectMapper().readValue(record.value(), PayRollReportDTO::class.java)
        println("PayRollReportDTO: $reportDTO")
        createReport(reportDTO)
    }

    private fun createReport(reportDTO : PayRollReportDTO) {
        val pathDir: Path = Paths.get(".data")

        if(!Files.exists(pathDir)) Files.createDirectory(pathDir)

        val pathFile: Path = Paths.get(".data/${reportDTO.cpf}-${Instant.now().toEpochMilli()}.txt")
        Files.writeString(pathFile, "CPF: ${reportDTO.cpf}\nFGTS: ${reportDTO.fgts}")
    }
}

