package uno.weichen.booklistingapp;

import java.util.ArrayList;

/**
 * Created by weichen on 8/28/16.
 * {@Book} represents an book item. It holds the details of the book such as title, author, etc.
 */
public class Book {
    private String mTitle;
    private String mSubTitle;
    private String mFirstAuthor;
    private String mPublishDate;
    private String mImageLink;
    private String mPreviewLink;



    public String getmTitle() {
        return mTitle;
    }

    public String getmImageLink() {
        return mImageLink;
    }

    public String getmPreviewLink() {
        return mPreviewLink;
    }

    public String getmFirstAuthor() {
        return mFirstAuthor;
    }


    public String getmPublishDate() {
        return mPublishDate;
    }


    public String getmSubTitle() {
        return mSubTitle;
    }

    public Book(String mTitle, String mSubTitle, String mFirstAuthor, String mPublishDate, String
        mImageLink, String mPreviewLink) {
        this.mTitle = mTitle;
        this.mSubTitle = mSubTitle;
        this.mFirstAuthor = mFirstAuthor;
        this.mPublishDate = mPublishDate;
        this.mImageLink = mImageLink;
        this.mPreviewLink = mPreviewLink;
    }

    @Override
    public String toString() {
        return "Book{" +
            "mTitle='" + mTitle + '\'' +
            ", mSubTitle='" + mSubTitle + '\'' +
            ", mFirstAuthor='" + mFirstAuthor + '\'' +
            ", mPublishDate='" + mPublishDate + '\'' +
            ", mImageLink='" + mImageLink + '\'' +
            ", mPreviewLink='" + mPreviewLink + '\'' +
            '}';
    }
}
