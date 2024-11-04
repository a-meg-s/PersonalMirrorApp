package com.example.uimirror.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.uimirror.database.models.Alarm;
import com.example.uimirror.database.models.Music;
import com.example.uimirror.database.models.Person;
import com.example.uimirror.database.typeconverters.ByteTypeConverter;
import com.example.uimirror.database.typeconverters.MusicTypeConverter;
import com.example.uimirror.database.typeconverters.SingleAlarmTypeConverter;
import java.lang.Boolean;
import java.lang.Byte;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalStateException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PersonDao_Impl implements PersonDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Person> __insertionAdapterOfPerson;

  private final ByteTypeConverter __byteTypeConverter = new ByteTypeConverter();

  private final SingleAlarmTypeConverter __singleAlarmTypeConverter = new SingleAlarmTypeConverter();

  private final MusicTypeConverter __musicTypeConverter = new MusicTypeConverter();

  public PersonDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPerson = new EntityInsertionAdapter<Person>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `Person` (`id`,`name`,`faceData`,`alarm`,`musicTracks`,`isPrimaryUser`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Person entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        final byte[] _tmp = __byteTypeConverter.fromListByte(entity.getFaceData());
        statement.bindBlob(3, _tmp);
        final String _tmp_1;
        if (entity.getAlarm() == null) {
          _tmp_1 = null;
        } else {
          _tmp_1 = __singleAlarmTypeConverter.fromAlarm(entity.getAlarm());
        }
        if (_tmp_1 == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, _tmp_1);
        }
        final String _tmp_2 = __musicTypeConverter.fromMusicList(entity.getMusicTracks());
        if (_tmp_2 == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, _tmp_2);
        }
        final Integer _tmp_3 = entity.isPrimaryUser() == null ? null : (entity.isPrimaryUser() ? 1 : 0);
        if (_tmp_3 == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, _tmp_3);
        }
      }
    };
  }

  @Override
  public Object insertAll(final List<Person> person, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPerson.insert(person);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPerson(final Person person, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPerson.insert(person);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPerson(final long id, final Continuation<? super Person> $completion) {
    final String _sql = "SELECT * FROM Person WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Person>() {
      @Override
      @Nullable
      public Person call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfFaceData = CursorUtil.getColumnIndexOrThrow(_cursor, "faceData");
          final int _cursorIndexOfAlarm = CursorUtil.getColumnIndexOrThrow(_cursor, "alarm");
          final int _cursorIndexOfMusicTracks = CursorUtil.getColumnIndexOrThrow(_cursor, "musicTracks");
          final int _cursorIndexOfIsPrimaryUser = CursorUtil.getColumnIndexOrThrow(_cursor, "isPrimaryUser");
          final Person _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final List<Byte> _tmpFaceData;
            final byte[] _tmp;
            _tmp = _cursor.getBlob(_cursorIndexOfFaceData);
            _tmpFaceData = __byteTypeConverter.toListByte(_tmp);
            final Alarm _tmpAlarm;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfAlarm)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfAlarm);
            }
            if (_tmp_1 == null) {
              _tmpAlarm = null;
            } else {
              _tmpAlarm = __singleAlarmTypeConverter.toAlarm(_tmp_1);
            }
            final List<Music> _tmpMusicTracks;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfMusicTracks)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfMusicTracks);
            }
            final List<Music> _tmp_3 = __musicTypeConverter.toMusicList(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<com.example.uimirror.database.models.Music>', but it was NULL.");
            } else {
              _tmpMusicTracks = _tmp_3;
            }
            final Boolean _tmpIsPrimaryUser;
            final Integer _tmp_4;
            if (_cursor.isNull(_cursorIndexOfIsPrimaryUser)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getInt(_cursorIndexOfIsPrimaryUser);
            }
            _tmpIsPrimaryUser = _tmp_4 == null ? null : _tmp_4 != 0;
            _result = new Person(_tmpId,_tmpName,_tmpFaceData,_tmpAlarm,_tmpMusicTracks,_tmpIsPrimaryUser);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPrimaryUser(final boolean isPrimaryUser,
      final Continuation<? super Person> $completion) {
    final String _sql = "SELECT * FROM Person WHERE isPrimaryUser = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final int _tmp = isPrimaryUser ? 1 : 0;
    _statement.bindLong(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Person>() {
      @Override
      @Nullable
      public Person call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfFaceData = CursorUtil.getColumnIndexOrThrow(_cursor, "faceData");
          final int _cursorIndexOfAlarm = CursorUtil.getColumnIndexOrThrow(_cursor, "alarm");
          final int _cursorIndexOfMusicTracks = CursorUtil.getColumnIndexOrThrow(_cursor, "musicTracks");
          final int _cursorIndexOfIsPrimaryUser = CursorUtil.getColumnIndexOrThrow(_cursor, "isPrimaryUser");
          final Person _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final List<Byte> _tmpFaceData;
            final byte[] _tmp_1;
            _tmp_1 = _cursor.getBlob(_cursorIndexOfFaceData);
            _tmpFaceData = __byteTypeConverter.toListByte(_tmp_1);
            final Alarm _tmpAlarm;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfAlarm)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfAlarm);
            }
            if (_tmp_2 == null) {
              _tmpAlarm = null;
            } else {
              _tmpAlarm = __singleAlarmTypeConverter.toAlarm(_tmp_2);
            }
            final List<Music> _tmpMusicTracks;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfMusicTracks)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfMusicTracks);
            }
            final List<Music> _tmp_4 = __musicTypeConverter.toMusicList(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<com.example.uimirror.database.models.Music>', but it was NULL.");
            } else {
              _tmpMusicTracks = _tmp_4;
            }
            final Boolean _tmpIsPrimaryUser;
            final Integer _tmp_5;
            if (_cursor.isNull(_cursorIndexOfIsPrimaryUser)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getInt(_cursorIndexOfIsPrimaryUser);
            }
            _tmpIsPrimaryUser = _tmp_5 == null ? null : _tmp_5 != 0;
            _result = new Person(_tmpId,_tmpName,_tmpFaceData,_tmpAlarm,_tmpMusicTracks,_tmpIsPrimaryUser);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getFaceDetectedPerson(final List<Byte> openCvFaceData,
      final Continuation<? super Person> $completion) {
    final String _sql = "SELECT * FROM Person WHERE faceData = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final byte[] _tmp = __byteTypeConverter.fromListByte(openCvFaceData);
    _statement.bindBlob(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Person>() {
      @Override
      @Nullable
      public Person call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfFaceData = CursorUtil.getColumnIndexOrThrow(_cursor, "faceData");
          final int _cursorIndexOfAlarm = CursorUtil.getColumnIndexOrThrow(_cursor, "alarm");
          final int _cursorIndexOfMusicTracks = CursorUtil.getColumnIndexOrThrow(_cursor, "musicTracks");
          final int _cursorIndexOfIsPrimaryUser = CursorUtil.getColumnIndexOrThrow(_cursor, "isPrimaryUser");
          final Person _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final List<Byte> _tmpFaceData;
            final byte[] _tmp_1;
            _tmp_1 = _cursor.getBlob(_cursorIndexOfFaceData);
            _tmpFaceData = __byteTypeConverter.toListByte(_tmp_1);
            final Alarm _tmpAlarm;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfAlarm)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfAlarm);
            }
            if (_tmp_2 == null) {
              _tmpAlarm = null;
            } else {
              _tmpAlarm = __singleAlarmTypeConverter.toAlarm(_tmp_2);
            }
            final List<Music> _tmpMusicTracks;
            final String _tmp_3;
            if (_cursor.isNull(_cursorIndexOfMusicTracks)) {
              _tmp_3 = null;
            } else {
              _tmp_3 = _cursor.getString(_cursorIndexOfMusicTracks);
            }
            final List<Music> _tmp_4 = __musicTypeConverter.toMusicList(_tmp_3);
            if (_tmp_4 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<com.example.uimirror.database.models.Music>', but it was NULL.");
            } else {
              _tmpMusicTracks = _tmp_4;
            }
            final Boolean _tmpIsPrimaryUser;
            final Integer _tmp_5;
            if (_cursor.isNull(_cursorIndexOfIsPrimaryUser)) {
              _tmp_5 = null;
            } else {
              _tmp_5 = _cursor.getInt(_cursorIndexOfIsPrimaryUser);
            }
            _tmpIsPrimaryUser = _tmp_5 == null ? null : _tmp_5 != 0;
            _result = new Person(_tmpId,_tmpName,_tmpFaceData,_tmpAlarm,_tmpMusicTracks,_tmpIsPrimaryUser);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllPersons(final Continuation<? super List<Person>> $completion) {
    final String _sql = "SELECT * FROM Person";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Person>>() {
      @Override
      @NonNull
      public List<Person> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfFaceData = CursorUtil.getColumnIndexOrThrow(_cursor, "faceData");
          final int _cursorIndexOfAlarm = CursorUtil.getColumnIndexOrThrow(_cursor, "alarm");
          final int _cursorIndexOfMusicTracks = CursorUtil.getColumnIndexOrThrow(_cursor, "musicTracks");
          final int _cursorIndexOfIsPrimaryUser = CursorUtil.getColumnIndexOrThrow(_cursor, "isPrimaryUser");
          final List<Person> _result = new ArrayList<Person>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Person _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final List<Byte> _tmpFaceData;
            final byte[] _tmp;
            _tmp = _cursor.getBlob(_cursorIndexOfFaceData);
            _tmpFaceData = __byteTypeConverter.toListByte(_tmp);
            final Alarm _tmpAlarm;
            final String _tmp_1;
            if (_cursor.isNull(_cursorIndexOfAlarm)) {
              _tmp_1 = null;
            } else {
              _tmp_1 = _cursor.getString(_cursorIndexOfAlarm);
            }
            if (_tmp_1 == null) {
              _tmpAlarm = null;
            } else {
              _tmpAlarm = __singleAlarmTypeConverter.toAlarm(_tmp_1);
            }
            final List<Music> _tmpMusicTracks;
            final String _tmp_2;
            if (_cursor.isNull(_cursorIndexOfMusicTracks)) {
              _tmp_2 = null;
            } else {
              _tmp_2 = _cursor.getString(_cursorIndexOfMusicTracks);
            }
            final List<Music> _tmp_3 = __musicTypeConverter.toMusicList(_tmp_2);
            if (_tmp_3 == null) {
              throw new IllegalStateException("Expected NON-NULL 'java.util.List<com.example.uimirror.database.models.Music>', but it was NULL.");
            } else {
              _tmpMusicTracks = _tmp_3;
            }
            final Boolean _tmpIsPrimaryUser;
            final Integer _tmp_4;
            if (_cursor.isNull(_cursorIndexOfIsPrimaryUser)) {
              _tmp_4 = null;
            } else {
              _tmp_4 = _cursor.getInt(_cursorIndexOfIsPrimaryUser);
            }
            _tmpIsPrimaryUser = _tmp_4 == null ? null : _tmp_4 != 0;
            _item = new Person(_tmpId,_tmpName,_tmpFaceData,_tmpAlarm,_tmpMusicTracks,_tmpIsPrimaryUser);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
