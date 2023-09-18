package org.example.model.database.dictionaryWork;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.example.data.entity.*;
import org.example.model.database.DBConstructor;

import java.util.ArrayList;
import java.util.List;

public class DBDictionaryWork extends DBConstructor implements IDBDictionaryWork {

    @Override
    public ESimpleWord loadSimpleWord(String wordID) throws Exception {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from simple_words where id = ?::integer", ESimpleWord.class);
            query.setParameter(1, wordID);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                return null;
            }

            ESimpleWord eWord = (ESimpleWord) query.getResultList().get(query.getFirstResult());

            transaction.commit();
            return eWord;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public ArrayList<ESimpleWord> loadSimpleWords() throws Exception {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from simple_words", ESimpleWord.class);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                return null;
            }

            List<ESimpleWord> eWords = query.getResultList();

            transaction.commit();
            return new ArrayList<>(eWords);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public void saveSimpleWord(String word, int typeID, String description) throws Exception {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from types_words where id = ?::integer", ETypeWord.class);
            query.setParameter(1, typeID);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                throw new Exception("Данный тип слова отсутствует в словаре");
            }

            query = entityManager.createNativeQuery("Select * from simple_words where word=?", ESimpleWord.class);
            query.setParameter(1, word);

            if (query.getResultList().size() != 0) {
                transaction.commit();
                throw new Exception("Введенное слово уже присутствует в словаре");
            }

            query = entityManager.createNativeQuery("Select * from spelling_variants where word=?", ESpellingVariants.class);
            query.setParameter(1, word);

            if (query.getResultList().size() != 0) {
                transaction.commit();
                throw new Exception("Введенное слово уже присутствует в словаре вариантов");
            }

            query = entityManager.createNativeQuery("Insert into simple_words (word, type_id) values (?, ?::integer)");
            query.setParameter(1, word).setParameter(2, typeID).executeUpdate();

            query = entityManager.createNativeQuery("Select * from simple_words where word=?", ESimpleWord.class);
            query.setParameter(1, word);

            ESimpleWord eSimpleWord = (ESimpleWord) query.getResultList().get(query.getFirstResult());

            query = entityManager.createNativeQuery("Insert into spelling_variants (word, simple_id, description) values (?, ?::integer, ?)");
            query.setParameter(1, word).setParameter(2, eSimpleWord.getId()).setParameter(3, description).executeUpdate();

            transaction.commit();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public void updateSimpleWord(int wordID, String word, int typeID, String description) throws Exception {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();



            Query query = entityManager.createNativeQuery("Select * from simple_words where id = ?::integer", ESimpleWord.class);
            query.setParameter(1, wordID);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                throw new Exception("Данное слово отсутствует в словаре");
            }

            ESimpleWord eSimpleWord = (ESimpleWord) query.getResultList().get(query.getFirstResult());

            query = entityManager.createNativeQuery("Select * from spelling_variants where simple_id = ?::integer and word = ?", ESpellingVariants.class);
            query.setParameter(1, wordID).setParameter(2, eSimpleWord.getWord());

            if (query.getResultList().size() == 0) {
                transaction.commit();
                throw new Exception("Данное слово отсутствует в словаре вариантов");
            }

            ESpellingVariants eSpellingVariants = (ESpellingVariants) query.getResultList().get(query.getFirstResult());

            if (description == null)
                description = eSpellingVariants.getDescription();

            if (word.isEmpty())
                word = eSimpleWord.getWord();

            if (typeID <= 0)
                typeID = eSimpleWord.getType_id();

            query = entityManager.createNativeQuery("Select * from types_words where id = ?::integer", ETypeWord.class);
            query.setParameter(1, typeID);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                throw new Exception("Данный тип слова отсутствует в словаре");
            }

            query = entityManager.createNativeQuery("Update spelling_variants set word = ?, description = ? where id = ?::integer");
            query.setParameter(1, word).setParameter(2, description).setParameter(3, eSpellingVariants.getId()).executeUpdate();

            query = entityManager.createNativeQuery("Update simple_words set word = ?, type_id = ?::integer where id = ?::integer");
            query.setParameter(1, word).setParameter(2, typeID).setParameter(3, wordID).executeUpdate();

            transaction.commit();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public void deleteSimpleWord(int wordID) throws Exception {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from simple_words where id = ?::integer", ESimpleWord.class);
            query.setParameter(1, wordID);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                throw new Exception("Введенное слово отсутствует в словаре");
            }

            ESimpleWord eSimpleWord = (ESimpleWord) query.getResultList().get(query.getFirstResult());

            query = entityManager.createNativeQuery("Select * from spelling_variants where simple_id = ?::integer and word = ?", ESpellingVariants.class);
            query.setParameter(1, wordID).setParameter(2, eSimpleWord.getWord());

            if (query.getResultList().size() == 0) {
                transaction.commit();
                throw new Exception("Введенное слово отсутствует в словаре вариантов");
            }

            ESpellingVariants eSpellingVariants = (ESpellingVariants) query.getResultList().get(query.getFirstResult());

            query = entityManager.createNativeQuery("Delete from spelling_variants where id = ?::integer");
            query.setParameter(1, eSpellingVariants.getId()).executeUpdate();

            query = entityManager.createNativeQuery("Select * from spelling_variants where simple_id = ?::integer", ESpellingVariants.class);
            query.setParameter(1, eSimpleWord.getId());

            if (query.getResultList().size() != 0) {
                eSpellingVariants = (ESpellingVariants) query.getResultList().get(query.getFirstResult());

                query = entityManager.createNativeQuery("Insert into simple_words (word, type_id) values (?, ?::integer)");
                query.setParameter(1, eSpellingVariants.getWord()).setParameter(2, eSimpleWord.getType_id()).executeUpdate();

                query = entityManager.createNativeQuery("Select * from simple_words where word = ?", ESimpleWord.class);
                query.setParameter(1, eSpellingVariants.getWord());

                eSimpleWord = (ESimpleWord) query.getResultList().get(query.getFirstResult());

                query = entityManager.createNativeQuery("Update spelling_variants set simple_id = ?::integer where simple_id = ?::integer");
                query.setParameter(1, eSimpleWord.getId()).setParameter(2, wordID).executeUpdate();
            }

            query = entityManager.createNativeQuery("Delete from simple_words where id = ?::integer");
            query.setParameter(1, wordID).executeUpdate();


            transaction.commit();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public ESpellingVariants loadSpellingWord(String wordID) throws Exception {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from spelling_variants where id = ?::integer", ESpellingVariants.class);

            query.setParameter(1, wordID);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                return null;
            }

            ESpellingVariants eWord = (ESpellingVariants) query.getResultList().get(query.getFirstResult());

            transaction.commit();
            return eWord;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public ArrayList<ESpellingVariants> loadSpellingWords() throws Exception {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from spelling_variants", ESpellingVariants.class);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                return null;
            }

            List<ESpellingVariants> eWords = query.getResultList();

            transaction.commit();
            return new ArrayList<>(eWords);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }


    @Override
    public void saveSpellingWord(String word, int simpleID, String description) throws Exception {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from simple_words where id = ?::integer", ESimpleWord.class);
            query.setParameter(1, simpleID);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                throw new Exception("Слово родитель отсутствует в словаре");
            }

            query = entityManager.createNativeQuery("Select * from spelling_variants where word = ?", ESpellingVariants.class);
            query.setParameter(1, word);

            if (query.getResultList().size() != 0) {
                transaction.commit();
                throw new Exception("Введенное слово уже присутствует в словаре вариантов");
            }


            query = entityManager.createNativeQuery("Insert into spelling_variants (word, simple_id, description) values (?, ?::integer, ?)");
            query.setParameter(1, word).setParameter(2, simpleID).setParameter(3, description).executeUpdate();

            transaction.commit();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }


    @Override
    public void updateSpellingWord(int wordID, String word, String description) throws Exception {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from spelling_variants where id = ?::integer", ESpellingVariants.class);
            query.setParameter(1, wordID);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                throw new Exception("Данное слово отсутствует в словаре вариантов");
            }

            ESpellingVariants eSpellingVariants = (ESpellingVariants) query.getResultList().get(query.getFirstResult());


            query = entityManager.createNativeQuery("Select * from simple_words where id = ?::integer", ESimpleWord.class);
            query.setParameter(1, eSpellingVariants.getSimple_id());

            if (query.getResultList().size() == 0) {
                transaction.commit();
                throw new Exception("Данное слово отсутствует в словаре");
            }

            ESimpleWord eSimpleWord = (ESimpleWord) query.getResultList().get(query.getFirstResult());


            if (word.isEmpty())
                word = eSpellingVariants.getWord();

            if (description == null)
                description = eSpellingVariants.getWord();

            query = entityManager.createNativeQuery("Update spelling_variants set word = ?, description = ? where id = ?::integer");
            query.setParameter(1, word).setParameter(2, description).setParameter(3, wordID).executeUpdate();

            query = entityManager.createNativeQuery("Update simple_words set word = ? where id = ?::integer and word = ?");
            query.setParameter(1, word).setParameter(2, eSimpleWord.getId()).setParameter(3, eSpellingVariants.getWord()).executeUpdate();

            transaction.commit();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public void deleteSpellingWord(int wordID) throws Exception {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from spelling_variants where id = ?::integer", ESpellingVariants.class);
            query.setParameter(1, wordID);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                throw new Exception("Введенное слово отсутствует в словаре вариантов");
            }

            ESpellingVariants eSpellingVariants = (ESpellingVariants) query.getResultList().get(query.getFirstResult());

            query = entityManager.createNativeQuery("Delete from spelling_variants where id = ?::integer");
            query.setParameter(1, wordID).executeUpdate();

            query = entityManager.createNativeQuery("Select * from simple_words where id = ?::integer and word = ?", ESimpleWord.class);
            query.setParameter(1, eSpellingVariants.getSimple_id()).setParameter(2, eSpellingVariants.getWord());

            if (query.getResultList().size() != 0) {
                ESimpleWord eSimpleWord = (ESimpleWord) query.getResultList().get(query.getFirstResult());

                query = entityManager.createNativeQuery("Select * from spelling_variants where simple_id = ?::integer", ESpellingVariants.class);
                query.setParameter(1, eSimpleWord.getId());

                eSpellingVariants = (ESpellingVariants) query.getResultList().get(query.getFirstResult());

                query = entityManager.createNativeQuery("Insert into simple_words (word, type_id) values (?, ?::integer)");
                query.setParameter(1, eSpellingVariants.getWord()).setParameter(2, eSimpleWord.getType_id()).executeUpdate();

                query = entityManager.createNativeQuery("Select * from simple_words where word = ?", ESimpleWord.class);
                query.setParameter(1, eSpellingVariants.getWord());

                int oldSimpleID = eSimpleWord.getId();

                eSimpleWord = (ESimpleWord) query.getResultList().get(query.getFirstResult());

                query = entityManager.createNativeQuery("Update spelling_variants set simple_id = ?::integer where simple_id = ?::integer");
                query.setParameter(1, eSimpleWord.getId()).setParameter(2, oldSimpleID).executeUpdate();

                query = entityManager.createNativeQuery("Delete from simple_words where id = ?::integer");
                query.setParameter(1, oldSimpleID).executeUpdate();
            }

            transaction.commit();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }

    @Override
    public ArrayList<ETypeWord> loadTypeWords() throws Exception {
        EntityManager entityManager = null;
        try {
            entityManager = entityManagerConstructor();

            transaction.begin();
            entityManager.joinTransaction();

            Query query = entityManager.createNativeQuery("Select * from types_words", ETypeWord.class);

            if (query.getResultList().size() == 0) {
                transaction.commit();
                return null;
            }

            List<ETypeWord> eWords = query.getResultList();

            transaction.commit();
            return new ArrayList<>(eWords);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("Ошибка: " + e.getMessage());
        } finally {
            assert entityManager != null;
            entityManager.close();
        }
    }
}
