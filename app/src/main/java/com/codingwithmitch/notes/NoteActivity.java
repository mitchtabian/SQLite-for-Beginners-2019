package com.codingwithmitch.notes;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codingwithmitch.notes.async.InsertAsyncTask;
import com.codingwithmitch.notes.async.UpdateAsyncTask;
import com.codingwithmitch.notes.models.Note;
import com.codingwithmitch.notes.persistence.AppDatabase;
import com.codingwithmitch.notes.persistence.NoteDao;
import com.codingwithmitch.notes.util.Utility;


public class NoteActivity extends AppCompatActivity implements
        View.OnClickListener,
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        TextWatcher
{

    private static final String TAG = "NoteActivity";
    private static final int EDIT_MODE_ENABLED = 1;
    private static final int EDIT_MODE_DISABLED = 0;

    //ui components
    private LinedEditText mLinedEditText;
    private ImageButton mCheck, mBackArrow;
    private RelativeLayout mCheckContainer, mBackArrowContainer;
    private EditText mEditTitle;
    private TextView mViewTitle;

    //vars
    private GestureDetector mGestureDetector;
    private int mMode = EDIT_MODE_DISABLED;
    private boolean mIsNewNote;
    private Note mNoteInitial = new Note();
    private Note mNoteFinal = new Note();
    private NoteDao mNoteDao;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        mLinedEditText = findViewById(R.id.note_text);
        mCheck = findViewById(R.id.toolbar_check);
        mBackArrow = findViewById(R.id.toolbar_back_arrow);
        mCheckContainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow_container);
        mEditTitle = findViewById(R.id.note_edit_title);
        mViewTitle = findViewById(R.id.note_text_title);

        mBackArrow.setOnClickListener(this);
        mCheck.setOnClickListener(this);
        mViewTitle.setOnClickListener(this);
        mGestureDetector = new GestureDetector(this, this);
        mLinedEditText.setOnTouchListener(this);
        mEditTitle.addTextChangedListener(this);
        mLinedEditText.addTextChangedListener(new ContentTextWatcher());
        mEditTitle.addTextChangedListener(new ContentTextWatcher());

        mNoteDao = AppDatabase.getDatabase(this).noteDataDao();

        if(getIncomingIntent()){
            setNewNoteProperties();
            enableEditMode();
        }
        else{
            setNoteProperties();
            disableContentInteraction();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mode", mMode);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMode = savedInstanceState.getInt("mode");
        if(mMode == EDIT_MODE_ENABLED){
            enableEditMode();
        }
    }


    private void saveChanges(){
        if(mIsNewNote){
            saveNewNote();
        }else{
            updateNote();
        }
    }

    public void saveNewNote() {
        new InsertAsyncTask(mNoteDao).execute(mNoteFinal);
    }

    public void updateNote() {
        new UpdateAsyncTask(mNoteDao).execute(mNoteFinal);
    }


    private void setNewNoteProperties(){
        mViewTitle.setText("Note Title");
        mEditTitle.setText("Note Title");
        appendNewLines();
    }

    private void setNoteProperties(){
        mViewTitle.setText(mNoteInitial.getTitle());
        mEditTitle.setText(mNoteInitial.getTitle());
        mLinedEditText.setText(mNoteInitial.getContent());
    }


    private boolean getIncomingIntent(){
        if(getIntent().hasExtra("selected_note")){
            Note incomingNote = getIntent().getParcelableExtra("selected_note");
            mNoteInitial.setTitle(incomingNote.getTitle());
            mNoteInitial.setTimestamp(incomingNote.getTimestamp());
            mNoteInitial.setContent(incomingNote.getContent());
            mNoteInitial.setUid(incomingNote.getUid());

            mNoteFinal.setTitle(incomingNote.getTitle());
            mNoteFinal.setTimestamp(incomingNote.getTimestamp());
            mNoteFinal.setContent(incomingNote.getContent());
            mNoteFinal.setUid(incomingNote.getUid());

            mIsNewNote = false;
            return false;
        }
        mIsNewNote = true;
        return true;
    }

    private void appendNewLines(){
        String text = mLinedEditText.getText().toString();
        StringBuilder sb = new StringBuilder();
        sb.append(text);
        for(int i = 0; i < 20; i++){
            sb.append("\n");
        }
        mLinedEditText.setText(sb.toString());
    }


    private void enableEditMode(){
        mBackArrowContainer.setVisibility(View.GONE);
        mCheckContainer.setVisibility(View.VISIBLE);

        mViewTitle.setVisibility(View.GONE);
        mEditTitle.setVisibility(View.VISIBLE);

        enableContentInteraction();

        showSoftkeyboard();

        mMode = EDIT_MODE_ENABLED;
    }


    private void disableEditMode(){
        Log.d(TAG, "disableEditMode: called.");
        hideSoftkeyboard();

        mBackArrowContainer.setVisibility(View.VISIBLE);
        mCheckContainer.setVisibility(View.GONE);

        mViewTitle.setVisibility(View.VISIBLE);
        mEditTitle.setVisibility(View.GONE);

        disableContentInteraction();

        mMode = EDIT_MODE_DISABLED;

        // Check if they typed anything into the note. Don't want to save an empty note.
        String temp = mLinedEditText.getText().toString();
        temp = temp.replace("\n", "");
        temp = temp.replace(" ", "");
        if(temp.length() > 0){
            mNoteFinal.setTitle(mEditTitle.getText().toString());
            mNoteFinal.setContent(mLinedEditText.getText().toString());
            String timestamp = Utility.getCurrentTimeStamp();
            mNoteFinal.setTimestamp(timestamp);

            // If the note was altered, save it.
            if(!mNoteFinal.getContent().equals(mNoteInitial.getContent())
                    || !mNoteFinal.getTitle().equals(mNoteInitial.getTitle())){
                Log.d(TAG, "disableEditMode: SAVING NOTE: " + mLinedEditText.getText().toString());
                saveChanges();
            }
        }
    }

    private void disableContentInteraction(){
        mLinedEditText.setKeyListener(null);
        mLinedEditText.setFocusable(false);
        mLinedEditText.setFocusableInTouchMode(false);
        mLinedEditText.setCursorVisible(false);
        mLinedEditText.clearFocus();
    }

    private void enableContentInteraction(){
        mLinedEditText.setKeyListener(new EditText(this).getKeyListener());
        mLinedEditText.setFocusable(true);
        mLinedEditText.setFocusableInTouchMode(true);
        mLinedEditText.setCursorVisible(true);
        mLinedEditText.requestFocus();
    }

    private void showSoftkeyboard(){
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                mLinedEditText.getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideSoftkeyboard(){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_back_arrow:{
                finish();
                break;
            }
            case R.id.toolbar_check:{
                disableEditMode();
                break;
            }
            case R.id.note_text_title:{
                enableEditMode();
                mEditTitle.requestFocus();
                mEditTitle.setSelection(mEditTitle.length());
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(mMode == EDIT_MODE_ENABLED){
            onClick(mCheck);
        }
        else{
            super.onBackPressed();
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Log.d(TAG, "onTouch: called.");
        if(v.getId() != R.id.toolbar_back_arrow
                && v.getId() != R.id.toolbar_check){
            if(v.getId() == R.id.note_text){
                if(mMode == EDIT_MODE_DISABLED){
                    return mGestureDetector.onTouchEvent(event);
                }

            }
        }
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        enableEditMode();
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mViewTitle.setText(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    private class ContentTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mNoteFinal.setContent(mLinedEditText.getText().toString());
            mNoteFinal.setTimestamp(Utility.getCurrentTimeStamp());
            mNoteFinal.setTitle(mEditTitle.getText().toString());
            Log.d(TAG, "onTextChanged: title: " + mNoteFinal.getTitle() + ", content: " + mNoteFinal.getContent());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

}









