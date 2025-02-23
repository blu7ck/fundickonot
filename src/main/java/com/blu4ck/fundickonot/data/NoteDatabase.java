package com.blu4ck.fundickonot.data;

public interface NoteDatabase {

    void createNote();  //Default ve Klasörler içinde not oluşturmak için
    void deleteNote();  //Not silme
    void getNotes();    //Notları görüntülemek için
    void updateNote();  //Notları güncellemek için
    void addNote(); //varolan klasör içerisinde not oluşturmak için gereklilik olursa kullanacağımız metod
}
