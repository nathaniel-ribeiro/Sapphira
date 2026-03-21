import java.io.File

class DataExportService {
    fun saveToCsv(fileName: String, headers: List<String>, data: Array<DoubleArray>) {
        File(fileName).bufferedWriter().use { writer ->
            writer.write(headers.joinToString(","))
            writer.newLine()
            data.forEach { row ->
                writer.write(row.joinToString(","))
                writer.newLine()
            }
        }
    }
}