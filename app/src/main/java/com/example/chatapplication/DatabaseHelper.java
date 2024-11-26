package com.example.chatapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chatApp.db";
    private static final int DATABASE_VERSION = 1;


    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_PROFILE_IMAGE_URL = "profileImageUrl";


    public static final String TABLE_CHAT_ROOMS = "chatRooms";
    public static final String COLUMN_CHAT_ID = "chatId";
    public static final String COLUMN_CHAT_NAME = "chatName";
    public static final String COLUMN_PARTICIPANTS = "participants";
    public static final String COLUMN_TIMESTAMP = "timestamp";


    public static final String TABLE_MESSAGES = "messages";
    public static final String COLUMN_MESSAGE_ID = "messageId";
    public static final String COLUMN_MESSAGE_TEXT = "messageText";
    public static final String COLUMN_MESSAGE_USER = "messageUser";
    public static final String COLUMN_MESSAGE_TIMESTAMP = "messageTimestamp";
    public static final String COLUMN_MESSAGE_IMAGE_URL = "messageImageUrl";
    public static final String COLUMN_CHAT_ROOM_ID = "chatRoomId";


    private static final String TABLE_CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_EMAIL + " TEXT UNIQUE, " + // Hacer único el email para evitar duplicados
                    COLUMN_PASSWORD + " TEXT, " +
                    COLUMN_PROFILE_IMAGE_URL + " TEXT);";

    private static final String TABLE_CREATE_CHAT_ROOMS =
            "CREATE TABLE " + TABLE_CHAT_ROOMS + " (" +
                    COLUMN_CHAT_ID + " TEXT PRIMARY KEY, " +
                    COLUMN_CHAT_NAME + " TEXT, " +
                    COLUMN_PARTICIPANTS + " TEXT, " +
                    COLUMN_TIMESTAMP + " INTEGER);";

    private static final String TABLE_CREATE_MESSAGES =
            "CREATE TABLE " + TABLE_MESSAGES + " (" +
                    COLUMN_MESSAGE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MESSAGE_TEXT + " TEXT, " +
                    COLUMN_MESSAGE_USER + " TEXT, " +
                    COLUMN_MESSAGE_TIMESTAMP + " INTEGER, " +
                    COLUMN_MESSAGE_IMAGE_URL + " TEXT, " +
                    COLUMN_CHAT_ROOM_ID + " TEXT);";


    private Context context;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TABLE_CREATE_USERS);
            db.execSQL(TABLE_CREATE_CHAT_ROOMS);
            db.execSQL(TABLE_CREATE_MESSAGES);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error creating tables: ", e);
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_ROOMS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
            onCreate(db);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error upgrading database: ", e);
        }
    }


    //Verificar usuario con la contraseña guardada
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID};
        String selection = COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {email, password}; // Usar la contraseña sin cifrar
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }

    //Obtener usuario actual
    public User getUser(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_PROFILE_IMAGE_URL};
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {userId};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int usernameIndex = cursor.getColumnIndex(COLUMN_USERNAME);
            int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);
            int profileImageUrlIndex = cursor.getColumnIndex(COLUMN_PROFILE_IMAGE_URL);
            if (idIndex >= 0 && usernameIndex >= 0 && emailIndex >= 0 && profileImageUrlIndex >= 0) {
                int id = cursor.getInt(idIndex);
                String username = cursor.getString(usernameIndex);
                String email = cursor.getString(emailIndex);
                String profileImageUrl = cursor.getString(profileImageUrlIndex);
                cursor.close();
                db.close();
                return new User(id, username, email, profileImageUrl);
            }
        }
        cursor.close();
        db.close();
        return null;
    }
   //Este método permite obtener usuario a traves de email
   public User getUserByEmail(String email) {
       SQLiteDatabase db = this.getReadableDatabase();
       String[] columns = {COLUMN_ID, COLUMN_USERNAME, COLUMN_EMAIL, COLUMN_PROFILE_IMAGE_URL};
       String selection = COLUMN_EMAIL + " = ?";
       String[] selectionArgs = {email};
       Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
       if (cursor.moveToFirst()) {
           int idIndex = cursor.getColumnIndex(COLUMN_ID);
           int usernameIndex = cursor.getColumnIndex(COLUMN_USERNAME);
           int emailIndex = cursor.getColumnIndex(COLUMN_EMAIL);
           int profileImageUrlIndex = cursor.getColumnIndex(COLUMN_PROFILE_IMAGE_URL);
           if (idIndex >= 0 && usernameIndex >= 0 && emailIndex >= 0 && profileImageUrlIndex >= 0) {
               int id = cursor.getInt(idIndex);
               String username = cursor.getString(usernameIndex);
               String emailStr = cursor.getString(emailIndex);
               String profileImageUrl = cursor.getString(profileImageUrlIndex);
               cursor.close();
               db.close();
               return new User(id, username, emailStr, profileImageUrl);
           }
       }
       cursor.close();
       db.close();
       return null;
   }

    //Guardar el id de usuaio cuando inicie sesion
    public void saveCurrentUserId(String userId) {
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("currentUserId", userId);
        editor.apply();
        Log.d("DatabaseHelper", "ID de usuario guardado en SharedPreferences: " + userId);
    }


    // Obtener el usuario actual
    public User getCurrentUser() {
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String currentUserId = prefs.getString("currentUserId", null);
        Log.d("DatabaseHelper", "ID de usuario en  SharedPreferences: " + currentUserId);
        if (currentUserId != null) {
            return getUser(currentUserId);
        } else {
            Log.e("DatabaseHelper", "Error: ID de usuario es nuloSharedPreferences");
            return null;
        }
    }



    // Agregar mensaje
    public void addMessage(MsgSend message, String chatRoomId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE_TEXT, message.getMsg());
        values.put(COLUMN_MESSAGE_USER, message.getUsername());
        values.put(COLUMN_MESSAGE_TIMESTAMP, message.getTime());
        values.put(COLUMN_MESSAGE_IMAGE_URL, message.getPictureMsg());
        values.put(COLUMN_CHAT_ROOM_ID, chatRoomId); // Agregar el ID de la sala de chat
        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }

    public List<MsgRec> getMessages(String chatRoomId) {
        List<MsgRec> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_CHAT_ROOM_ID + " = ?";
        String[] selectionArgs = {chatRoomId};
        Cursor cursor = db.query(TABLE_MESSAGES, null, selection, selectionArgs, null, null, COLUMN_MESSAGE_TIMESTAMP + " ASC");
        if (cursor.moveToFirst()) {
            do {
                int messageTextIndex = cursor.getColumnIndex(COLUMN_MESSAGE_TEXT);
                int messageUserIndex = cursor.getColumnIndex(COLUMN_MESSAGE_USER);
                int messageTimestampIndex = cursor.getColumnIndex(COLUMN_MESSAGE_TIMESTAMP);
                int messageImageUrlIndex = cursor.getColumnIndex(COLUMN_MESSAGE_IMAGE_URL);
                if (messageTextIndex >= 0 && messageUserIndex >= 0 && messageTimestampIndex >= 0 && messageImageUrlIndex >= 0) {
                    String messageText = cursor.getString(messageTextIndex);
                    String messageUser = cursor.getString(messageUserIndex);
                    long messageTimestamp = cursor.getLong(messageTimestampIndex);
                    String messageImageUrl = cursor.getString(messageImageUrlIndex);
                    messages.add(new MsgRec(messageText, messageUser, messageImageUrl, "1", messageTimestamp));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messages;
    }

    // Agregar sala de chat
    public boolean createChatRoom(ChatRoom chatRoom) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CHAT_ID, chatRoom.getChatId());
        values.put(COLUMN_CHAT_NAME, chatRoom.getChatName());
        values.put(COLUMN_PARTICIPANTS, String.join(",", chatRoom.getParticipants())); // Lista de participantes como texto
        values.put(COLUMN_TIMESTAMP, chatRoom.getLastMessageTimestamp());


        long result = db.insert(TABLE_CHAT_ROOMS, null, values);
        db.close();
        return result != -1;
    }

    // Obtener todas las salas de chat
    public List<ChatRoom> getChatRooms() {
        List<ChatRoom> chatRooms = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHAT_ROOMS, null, null, null, null, null, COLUMN_TIMESTAMP + " DESC");

        if (cursor.moveToFirst()) {
            do {
                // Obtener los índices de las columnas
                int chatIdIndex = cursor.getColumnIndex(COLUMN_CHAT_ID);
                int chatNameIndex = cursor.getColumnIndex(COLUMN_CHAT_NAME);
                int participantsIndex = cursor.getColumnIndex(COLUMN_PARTICIPANTS);
                int timestampIndex = cursor.getColumnIndex(COLUMN_TIMESTAMP);

                // Verificar que los índices sean válidos (>= 0)
                if (chatIdIndex >= 0 && chatNameIndex >= 0 && participantsIndex >= 0 && timestampIndex >= 0) {
                    String chatId = cursor.getString(chatIdIndex);
                    String chatName = cursor.getString(chatNameIndex);
                    String participants = cursor.getString(participantsIndex);
                    long timestamp = cursor.getLong(timestampIndex);

                    // Convierte la lista de participantes de texto a lista
                    List<String> participantList = new ArrayList<>();
                    if (participants != null && !participants.isEmpty()) {
                        String[] participantArray = participants.split(",");
                        for (String participant : participantArray) {
                            participantList.add(participant);
                        }
                    }

                    chatRooms.add(new ChatRoom(chatId, participantList, "", timestamp, chatName));
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return chatRooms;
    }


    // Actualizar la URL de la imagen de perfil del usuario
    public boolean updateUserProfileImage(String userId, String newImagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROFILE_IMAGE_URL, newImagePath); // Actualiza la URL de la imagen
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {userId};
        int rowsAffected = db.update(TABLE_USERS, values, selection, selectionArgs);
        db.close();
        return rowsAffected > 0; // Devuelve true si se actualizó correctamente
    }

}
