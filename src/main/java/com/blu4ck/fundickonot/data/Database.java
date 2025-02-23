package com.blu4ck.fundickonot.data;

public interface Database extends FolderDatabase, SubFolderDatabase, NoteDatabase {

    void initializeDatabase(); // database başlatma metotu
    void  Connection(); //bağlantı ayarları
}
