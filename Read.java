package HW2;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import java.util.*;

public class Read
{
    private static final String INDEX_DIR = "FileIndicizzati";

    public static void main(String[] args) throws Exception
    {
        Scanner sc= new Scanner(System.in);
        System.out.print("Vuoi cercare nel nome o nel contenuto del file? ");
        String str= sc.nextLine();
        String testo = null;
        TopDocs foundDocs = null;
        IndexSearcher searcher = null;

        if (str.equals("nome")){
            System.out.print("Inserisci il testo da cercare nel nome: ");
            testo = sc.nextLine();
            searcher = creaSearcher();
            foundDocs = ricercaTitolo(searcher,testo);

        } else if (str.equals("contenuto")) {
            Scanner sca= new Scanner(System.in);
            System.out.print("Inserisci il testo da cercare nel contenuto: ");
            testo = sca.nextLine();
            searcher = creaSearcher();
            foundDocs = ricercaContenuto(searcher,testo);
        }
        System.out.println("Risultati trovati: " + foundDocs.totalHits);

    }
    private static TopDocs ricercaTitolo(IndexSearcher searcher,String s) throws Exception
    {

        QueryParser qp = new QueryParser("nome", new WhitespaceAnalyzer());
        Query query = qp.parse(s);
        TopDocs hits = searcher.search(query, 10);
        for (int i=0; i<hits.scoreDocs.length; i++){
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            Document d = searcher.doc(scoreDoc.doc);
            System.out.println("Nome file: " + d.get("nome") + ", Score : " + scoreDoc.score);
        }
        return hits;
    }
    private static TopDocs ricercaContenuto(IndexSearcher searcher,String s) throws Exception {

        QueryParser qp = new QueryParser("contenuto", new StandardAnalyzer());
        Query query = qp.parse(s);
        TopDocs hits = searcher.search(query, 100);
        for (int i = 0; i < hits.scoreDocs.length; i++) {
            ScoreDoc scoreDoc = hits.scoreDocs[i];
            Document d = searcher.doc(scoreDoc.doc);
            System.out.println("Nome file: " + d.get("nome") + ", Score : " + scoreDoc.score);
        }
        return hits;
    }

    private static IndexSearcher creaSearcher() throws IOException
    {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }

}
