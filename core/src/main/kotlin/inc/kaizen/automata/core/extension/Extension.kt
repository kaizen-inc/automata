package inc.kaizen.automata.core.extension

import inc.kaizen.automata.core.render.TemplateRenderer
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.copyToRecursively

fun String.toPath(): Path = Paths.get(this)

@OptIn(ExperimentalPathApi::class)
fun Path.copyDefaultTemplates() = run {
    val destinationDir = toFile()
    if (destinationDir.exists() || destinationDir.listFiles()?.isNotEmpty() == true) {
        return@run
    }

    if (!toFile().exists()) {
        toFile().mkdirs()
    }
    val uri = TemplateRenderer::class.java.getResource("/template")?.toURI()
    if (uri != null) {
        val myPath: Path = if (uri.scheme.equals("jar")) {
            val fileSystem: FileSystem = FileSystems.newFileSystem(uri, emptyMap<String, Any>())
            fileSystem.getPath("/template")
        } else {
            Paths.get(uri)
        }

        myPath.copyToRecursively(this, followLinks = true)
    } else {
        throw Exception("Could not find default templates in source code")
    }
}

fun main() {
    val templatePath: Path = System.getProperty("user.home")
        .toPath()
        .resolve("automata")

    templatePath.copyDefaultTemplates()
}


//fun String.readResource(): String? =
//    AutomataModuleDescriptionProvider::class.java.getResource(this)?.readText()

//@Throws(URISyntaxException::class, IOException::class)
//fun String.resource(): String? {
//    val url = AutomataModuleDescriptionProvider::class.java
//        .getClassLoader()
//        .getResource("template")
//    return if (url != null) {
//        if ("jar" == url.toURI()?.scheme) {
//            val fileSystem: FileSystem = FileSystems.newFileSystem(url.toURI(), Collections.emptyMap<String, String>(), null)
//            fileSystem.getPath(this).normalize().toString()
//        } else {
//            Paths.get(url.toURI()).normalize().toString()
//        }
//    } else {
//        null
//    }
//}

//@Throws(URISyntaxException::class, IOException::class)
//fun String.copyTo(target: Path) {
//    val url = AutomataModuleDescriptionProvider::class.java
//        .getResourceAsStream(this)
//        .getResource(this)
//    val fileSystem: FileSystem = FileSystems.newFileSystem(
//        url?.toURI(),
//        Collections.emptyMap<String, String>()
//    )
//    val jarPath = fileSystem.getPath(this)
//    Files.walkFileTree(jarPath, object : SimpleFileVisitor<Path>() {
//
//        @Throws(IOException::class)
//        override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
//            val currentTarget = target.resolve(jarPath.relativize(dir).toString())
//            currentTarget.let { Files.createDirectories(it) }
//            return FileVisitResult.CONTINUE
//        }
//
//        @Throws(IOException::class)
//        override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
//            Files.copy(file, target.resolve(jarPath.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING)
//            return FileVisitResult.CONTINUE
//        }
//    })

//    FileUtils.copyInputStreamToFile(url, target.toFile())
//    FileUtils.copyURLToFile(url, target.toFile())

//    ZipFile(target.resolve("templates.zip").toFile()).use { zip ->
//        zip.entries().asSequence().forEach { entry ->
//            zip.getInputStream(entry).use { input ->
//                val filePath = target.resolve(entry.name).normalize().toString()
//                if (!entry.isDirectory) {
//                    // if the entry is a file, extracts it
//                    extractFile(input, filePath)
//                } else {
//                    // if the entry is a directory, make the directory
//                    val dir = File(filePath)
//                    dir.mkdir()
//                }
//            }
//        }
//    }
//}

//private const val BUFFER_SIZE = 4096
//@Throws(IOException::class)
//private fun extractFile(inputStream: InputStream, destFilePath: String) {
//    val bos = BufferedOutputStream(FileOutputStream(destFilePath))
//    val bytesIn = ByteArray(BUFFER_SIZE)
//    var read: Int
//    while (inputStream.read(bytesIn).also { read = it } != -1) {
//        bos.write(bytesIn, 0, read)
//    }
//    bos.close()
//}
