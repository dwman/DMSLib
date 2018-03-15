package com.dwman.dms.bean;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ldw on 2018/3/13.
 */

public class ProgramInfo implements Parcelable {
   public static final int TYPE_MOVIE = 0X001;
   public static final int TYPE_PUBLICAD =0X002;
   public static final int TYPE_BUSINESSAD = 0X003;
   public static final int TYPE_MUSIC = 0X004;
   public static final int TYPE_SUBAD = 0X005;
   public static final int TYPE_AUDIO_RECORD =0X006;
   public static final int TYPE_SUBTITLE_RECORD = 0X007;
   public static final int TYPE_BUSINESSAD_AUTH = 0X008;



    public String mProgramId;			// 影片ID
    public long mPprogramSize;			// 影片大小
    public String mProgramName;		// 影片名称
    public String  mType;		// 影片类型
    public String  mPath;// 影片绝对路径
//    public TaProgramType programType;	///< 节目类型: 影片、公益广告、商业广告
    public int mProgramType;

    public int mProgramDuration;	// 影片时长(秒)
    public String mBitStream;		///< 码率: B/s  ( 文字广告类型 )
    public String mAspectRatio;	///< 分辨率   ( 文字广告显示速度 )
    public String  mIssuer;		///< 发行商   ( 文字广告显示间隔 )
    public String  mIssueDate;	// 发行时间
    public String  mCreator;
    public String  mCountry;	// 国家// use to be dms av format !!!

    public String mDubLanguage;	///< 配音语言  ( 文字广告编码类型 )
    public String mSubtitleLanguage;	///< 字幕语言  ( 文字广告内容 )
    public String  mSubtitle;
    // 字幕
    public String mVideoFile;		// 视频文件
    public String mAudioFile;		// 音频文件
    public String mVXkzFile;	// 许可证视频文件
    public String mAXkzFile;	// 许可证音频文件

    public String mAudioType;		///< 音频编码类型    ( 文字广告显示位置 )
    public int mAudioChannels;			///< 音频声道数
    public int mSubstreamNr;		///< 音频流序号
    public boolean mHaveAudioRecord;

    public String  mAudioDefaultName;               //缺省语言名称
    public String mAudioRecordAmount; //音频配音数(多配音文件)
    public boolean mHaveSutitleRecord;
    public String  mSubtitleRecordAmount;                      //字幕数量(多字幕)
    public String contentType;

    public ProgramInfo() {
    }

    protected ProgramInfo(Parcel in) {
        mProgramId = in.readString();
        mPprogramSize = in.readLong();
        mProgramName = in.readString();
        mType = in.readString();
        mPath = in.readString();
        mProgramType = in.readInt();
        mProgramDuration = in.readInt();
        mBitStream = in.readString();
        mAspectRatio = in.readString();
        mIssuer = in.readString();
        mIssueDate = in.readString();
        mCreator = in.readString();
        mCountry = in.readString();
        mDubLanguage = in.readString();
        mSubtitleLanguage = in.readString();
        mSubtitle = in.readString();
        mVideoFile = in.readString();
        mAudioFile = in.readString();
        mVXkzFile = in.readString();
        mAXkzFile = in.readString();
        mAudioType = in.readString();
        mAudioChannels = in.readInt();
        mSubstreamNr = in.readInt();
        mHaveAudioRecord = in.readByte() != 0;
        mAudioDefaultName = in.readString();
        mAudioRecordAmount = in.readString();
        mHaveSutitleRecord = in.readByte() != 0;
        mSubtitleRecordAmount = in.readString();
        contentType = in.readString();
    }

    public static final Creator<ProgramInfo> CREATOR = new Creator<ProgramInfo>() {
        @Override
        public ProgramInfo createFromParcel(Parcel in) {
            return new ProgramInfo(in);
        }

        @Override
        public ProgramInfo[] newArray(int size) {
            return new ProgramInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mProgramId);
        parcel.writeLong(mPprogramSize);
        parcel.writeString(mProgramName);
        parcel.writeString(mType);
        parcel.writeString(mPath);
        parcel.writeInt(mProgramType);
        parcel.writeInt(mProgramDuration);
        parcel.writeString(mBitStream);
        parcel.writeString(mAspectRatio);
        parcel.writeString(mIssuer);
        parcel.writeString(mIssueDate);
        parcel.writeString(mCreator);
        parcel.writeString(mCountry);
        parcel.writeString(mDubLanguage);
        parcel.writeString(mSubtitleLanguage);
        parcel.writeString(mSubtitle);
        parcel.writeString(mVideoFile);
        parcel.writeString(mAudioFile);
        parcel.writeString(mVXkzFile);
        parcel.writeString(mAXkzFile);
        parcel.writeString(mAudioType);
        parcel.writeInt(mAudioChannels);
        parcel.writeInt(mSubstreamNr);
        parcel.writeByte((byte) (mHaveAudioRecord ? 1 : 0));
        parcel.writeString(mAudioDefaultName);
        parcel.writeString(mAudioRecordAmount);
        parcel.writeByte((byte) (mHaveSutitleRecord ? 1 : 0));
        parcel.writeString(mSubtitleRecordAmount);
        parcel.writeString(contentType);
    }
}
