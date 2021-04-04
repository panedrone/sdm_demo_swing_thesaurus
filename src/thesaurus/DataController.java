package thesaurus;

import java.util.List;

import com.sqldalmaker.DataStoreManager;
import com.sqldalmaker.thesaurus.dao.ThesaurusDao;
import com.sqldalmaker.thesaurus.dto.RelatedWord;
import com.sqldalmaker.thesaurus.dto.Word;

public class DataController {

    private static DataStoreManager dm = new DataStoreManager();

    public static List<RelatedWord> getRelatedWords(Word word) throws Exception {
        dm.connect();
        ThesaurusDao dao = dm.createThesaurusDao();
        try {
            return dao.getRelatedWords(word.getWId());
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            dm.close();
        }
    }

    public static Integer getTotalWordsCount() throws Exception {
        dm.connect();
        ThesaurusDao dao = dm.createThesaurusDao();
        try {
            return dao.getTotalWordsCount();
        } catch (Throwable e) {
            e.printStackTrace();
            throw new Exception(e);
        } finally {
            dm.close();
        }
    }

    public static List<Word> getWordsByKey(String key) throws Exception {
        dm.connect();
        ThesaurusDao dao = dm.createThesaurusDao();
        try {
            String key1 = key + "%";
            return dao.getWordsByKey(key1);
        } finally {
            dm.close();
        }
    }
}
