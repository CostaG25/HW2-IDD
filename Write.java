package HW2;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;


public class Write
{
    public static void main(String[] args)
    {
        long startTime = System.currentTimeMillis();
        final Path docDir = Paths.get("FileTxt");

        try
        {

            Directory dir = FSDirectory.open( Paths.get("FileIndicizzati") );

            Analyzer analyzer =  CustomAnalyzer.builder(docDir)
                    .withTokenizer(WhitespaceTokenizerFactory.class)
                    .addTokenFilter(LowerCaseFilterFactory.class)
                    .addTokenFilter(StopFilterFactory.class,"ignoreCase", "true", "words", "stopwords.txt", "format", "wordset" )
                    .addTokenFilter(WordDelimiterGraphFilterFactory.class).build();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(OpenMode.CREATE_OR_APPEND);
            IndexWriter writer = new IndexWriter(dir, config);
            indexDocs(writer, docDir);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println(" Tempo impiegato per l'indicizzazione: " +(endTime-startTime)+" ms");
    }

    static void indexDoc(IndexWriter writer,Path file) throws IOException{

        Document doc = new Document();
        String nomeFileIniziale = file.getFileName().toString();
        String nomeFileFinale = nomeFileIniziale.replaceAll(".txt", "");

        doc.add(new TextField("nome", nomeFileFinale , Store.YES));
        doc.add(new TextField("contenuto", new String(Files.readAllBytes(file)), Store.YES));

        writer.addDocument(doc);
        writer.commit();

    }

    static void indexDocs(final IndexWriter writer, Path path) throws IOException
    {
        if (Files.isDirectory(path))
        {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    try
                    {
                        indexDoc(writer, file);
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else {
            indexDoc(writer, path);
        }
    }
}
