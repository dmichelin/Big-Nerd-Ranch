package daniel.photogallery;

import android.net.Uri;

/** This object holds the data for each item in the gallery
 * Created by daniel on 7/11/16.
 */
public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    /**
     * Creates a link to the photo that can be clicked on within the app
     * @return
     */
    public Uri getPhotoPageUri(){
        return Uri.parse("http://www.flickr.com/photos").buildUpon().appendPath(mOwner).appendPath(mId).build();
    }

    private String mOwner;

    @Override
    public String toString(){
        return mCaption;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
