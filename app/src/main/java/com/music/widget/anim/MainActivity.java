package com.music.widget.anim;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.music.widget.version1.MusicWidgetRotateImageView;
import com.music.widget.version2.CustomScaleAnimView;
import com.music.widget.version3.CircleAlbumSwitchView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mStartAnim1, mStartAnim2, mStartAnim3;

    private MusicWidgetRotateImageView mAnimOne;

    private CustomScaleAnimView mAnimTwo;

    private CircleAlbumSwitchView mAnimThree;

    private Bitmap mAlbumOneBmp;
    private Bitmap mAlbumTwoBmp;

    private int mAudioId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartAnim1 = (Button) findViewById(R.id.start_anim_verison1);
        mStartAnim2 =(Button) findViewById(R.id.start_anim_verison2);
        mStartAnim3 = (Button) findViewById(R.id.start_anim_verison3);

        mAnimOne = (MusicWidgetRotateImageView) findViewById(R.id.album_anim_verison1);
        mAnimTwo = (CustomScaleAnimView) findViewById(R.id.album_anim_verison2);
        mAnimThree = (CircleAlbumSwitchView) findViewById(R.id.album_anim_verison3);

        mStartAnim1.setOnClickListener(this);
        mStartAnim2.setOnClickListener(this);
        mStartAnim3.setOnClickListener(this);

        mAlbumOneBmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.adele_album);
        mAlbumTwoBmp = BitmapFactory.decodeResource(getResources(),
                R.drawable.lana_del_ray_album);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_anim_verison1:
                startAnim(1);
                break;
            case R.id.start_anim_verison2:
                startAnim(2);
                break;
            case R.id.start_anim_verison3:
                startAnim(3);
                break;
            default:break;
        }
    }

    private void startAnim(int type){
        mAnimOne.setVisibility(type == 1 ? View.VISIBLE : View.GONE);
        mAnimTwo.setVisibility(type == 2 ? View.VISIBLE : View.GONE);
        mAnimThree.setVisibility(type == 3 ? View.VISIBLE : View.GONE);

        if(mAlbumOneBmp == null || mAlbumOneBmp.isRecycled()){
            mAlbumOneBmp = BitmapFactory.decodeResource(getResources(),
                    R.drawable.adele_album);
        }

        if(mAlbumTwoBmp == null || mAlbumTwoBmp.isRecycled()){
            mAlbumTwoBmp = BitmapFactory.decodeResource(getResources(),
                    R.drawable.lana_del_ray_album);
        }

        if(mAudioId > 10){
            mAudioId =1;
        }

        if(type == 1){
            mAnimOne.setAudioId(mAudioId);
            mAnimOne.setAlbumArtBitmap(mAudioId % 2 == 0 ? mAlbumOneBmp : mAlbumTwoBmp);
        } else if(type == 2){
            mAnimTwo.setAudioId(mAudioId);
            mAnimTwo.setAlbumArtBitmap(mAudioId % 2 == 0 ? mAlbumOneBmp : mAlbumTwoBmp);
        } else if(type == 3){
            mAnimThree.setAudioId(mAudioId);
            mAnimThree.setPlayingState(true);
            mAnimThree.setAlbumArtBitmap(resizeBitmap(mAudioId % 2 == 0 ? mAlbumOneBmp : mAlbumTwoBmp));
        }
        mAudioId ++;
    }

    private Bitmap resizeBitmap(Bitmap bitmap){
        if (bitmap.getWidth() > 520 || bitmap.getHeight() > 520) {
            float scaleY = (float)(520) / bitmap.getWidth();
            float scaleX = (float)(520) / bitmap.getHeight();
            float scale = scaleY > scaleX ? scaleY : scaleX;
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
            if (bitmap != resizeBmp) {
                bitmap.recycle();
                bitmap = resizeBmp;
            }
        }
        return bitmap;
    }
}
