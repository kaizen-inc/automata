package inc.kaizen.automata.module.extension

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.MustacheFactory
import inc.kaizen.automata.module.ide.AutomataModuleDescriptionProvider
import inc.kaizen.automata.module.ide.Variable
import org.apache.commons.io.FileUtils
import java.io.IOException
import java.io.StringWriter
import java.net.URISyntaxException
import java.nio.file.*
import java.util.*


fun String.toPath(): Path = Paths.get(this)

fun String.readResource(): String? =
    AutomataModuleDescriptionProvider::class.java.getResource(this)?.readText()

fun Path.renderTemplate(scopes: Map<String, Any>): String {
    return StringWriter().use {
        val mf: MustacheFactory = DefaultMustacheFactory()
        val mustache = mf.compile(toFile().bufferedReader(), toFile().name)
        mustache.execute(it, scopes)
    }.toString()
}

@Throws(URISyntaxException::class, IOException::class)
fun String.resource(): String? {
    val url = AutomataModuleDescriptionProvider::class.java
        .getClassLoader()
        .getResource("template")
    return if (url != null) {
        if ("jar" == url.toURI()?.scheme) {
            val fileSystem: FileSystem = FileSystems.newFileSystem(url.toURI(), Collections.emptyMap<String, String>(), null)
            fileSystem.getPath(this).normalize().toString()
        } else {
            Paths.get(url.toURI()).normalize().toString()
        }
    } else {
        null
    }
}

@Throws(URISyntaxException::class, IOException::class)
fun String.copyTo(target: Path) {
    val url = AutomataModuleDescriptionProvider::class.java
        .getClassLoader()
        .getResource(this)
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

    FileUtils.copyURLToFile(url, target.toFile())

}
