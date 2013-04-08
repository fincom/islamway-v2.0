package com.symbyo.islamway.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.symbyo.islamway.R;
import com.symbyo.islamway.Utils;
import com.symbyo.islamway.domain.Scholar;
import com.symbyo.islamway.domain.Section;
import org.eclipse.jdt.annotation.NonNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class ScholarsAdapter extends BaseAdapter implements Filterable {

	private final Context mContext;

	/**
	 * List of scholars returned by this adapter.
	 */
	private List<Scholar> mScholars;

	private Utils.ArrayFilter<Scholar> mFilter;

	private final int ITEM_LAYOUT = R.layout.scholar_list_item;

	public ScholarsAdapter( @NonNull Context context, @NonNull Section section )
	{
		super();
		mContext = context;
		mScholars = section.getSectionScholars();

	}

	@Override
	public int getCount()
	{
		int size = 0;
		if ( mScholars != null ) {
			size = mScholars.size();
		}
		return size;
	}

	@Override
	public Scholar getItem( int position )
	{
		Scholar scholar = null;
		if ( mScholars != null ) {
			scholar = mScholars.get( position );
		}
		return scholar;
	}

	@Override
	public long getItemId( int position )
	{
		return position;
	}

	@Override
	public View getView( int position, View convertView, ViewGroup parent )
	{
		if ( convertView == null ) {
			convertView = LayoutInflater.from( mContext ).inflate( ITEM_LAYOUT,
																   null );
		}

		ViewHolder holder;

		if ( convertView.getTag() == null
				|| !(convertView.getTag() instanceof ViewHolder) ) {
			holder = new ViewHolder();
			holder.image = (ImageView) convertView
					.findViewById( R.id.scholar_image );
			holder.title = (TextView) convertView
					.findViewById( R.id.scholar_name );
			convertView.setTag( holder );
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Scholar scholar = getItem( position );
		holder.title.setText( scholar.getName() );
		Bitmap bmp = getThumbnail( scholar.getImageFileName() );
		if ( bmp == null ) {
			Drawable placeHolder = mContext.getResources().getDrawable(
					R.drawable.scholar_placeholder );
			holder.image.setImageDrawable( placeHolder );
			// download the scholar image
			if ( scholar.getImageUrl() != null ) {
				downloadImage( scholar.getImageUrl(),
							   scholar.getImageFileName(), holder.image );
			}
		} else {
			holder.image.setImageBitmap( bmp );
		}
		return convertView;
	}

	private Bitmap getThumbnail( String file_name )
	{
		Bitmap bitmap = null;
		if ( file_name != null && !file_name.isEmpty() ) {
			InputStream inStream = null;
			try {
				inStream = mContext.openFileInput( file_name );
				bitmap = BitmapFactory.decodeStream( inStream );
			} catch ( FileNotFoundException e ) {
			} finally {
				try {
					if ( inStream != null ) {
						inStream.close();
					}
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}

		return bitmap;
	}

	private void downloadImage(
			final String url, final String file_name,
			final ImageView image_view )
	{
		image_view.setTag( url );
		AsyncTask<Void, Void, String> task =
				new AsyncTask<Void, Void, String>() {

					@Override
					protected String doInBackground( Void... params )
					{
						return doDownloadImage( url );
					}

					private String doDownloadImage( String imageUrl )
					{
						BufferedInputStream reader = null;
						BufferedOutputStream writer = null;
						HttpURLConnection cnn = null;
						try {
							// download the file
							URL url = new URL( imageUrl );
							cnn = (HttpURLConnection) url.openConnection();
							reader = new BufferedInputStream(
									cnn.getInputStream() );
							//bmp = BitmapFactory.decodeStream( reader );
							// write it into the internal memory.
							byte[] buffer = new byte[1024];
							writer = new BufferedOutputStream(
									mContext.openFileOutput(
											file_name,
											Context.MODE_PRIVATE ),
									buffer.length );
							int read;
							while ( (read = reader.read( buffer, 0,
														 buffer.length )) != -1 ) {
								writer.write( buffer, 0, read );
							}

						} catch ( MalformedURLException e ) {
							e.printStackTrace();
						} catch ( IOException e ) {
							e.printStackTrace();
						} finally {
							try {
								if ( reader != null ) {
									reader.close();
								}
								if ( writer != null ) {
									writer.close();
								}
								if ( cnn != null ) {
									cnn.disconnect();
								}
							} catch ( IOException e ) {
							}
						}

						return file_name;
					}

					@Override
					protected void onPostExecute( String image_file )
					{
						super.onPostExecute( image_file );
						String tag = (String) image_view.getTag();
						if ( tag != null && tag.equals( url ) ) {
							Bitmap bmp = null;
							try {
								bmp = BitmapFactory.decodeStream(
										mContext.openFileInput(
												image_file ) );
							} catch ( FileNotFoundException e ) {
								e.printStackTrace();
							}
							if ( bmp != null ) {
								image_view.setImageBitmap( bmp );
							}
						}
					}

				};
		/*ThreadPoolExecutor executor =
				(ThreadPoolExecutor) AsyncTask.THREAD_POOL_EXECUTOR;
		executor.setCorePoolSize( 5 );
		executor.setMaximumPoolSize( 10 );
		executor.setRejectedExecutionHandler(
				new ThreadPoolExecutor.DiscardOldestPolicy() );
		task.executeOnExecutor( executor );*/
		task.execute();
	}

	@Override
	public boolean areAllItemsEnabled()
	{
		return true;
	}

	private static class ViewHolder {
		public TextView  title;
		public ImageView image;
	}

    /*private class ArrayFilter extends Filter {

        private List<Scholar> mOriginalValues;
        @Override
        protected FilterResults performFiltering( CharSequence prefix )
        {
            FilterResults results = new FilterResults();

            if ( mOriginalValues == null ) {
                synchronized ( mLock ) {
                    mOriginalValues = new ArrayList<Scholar>( mScholars );
                }
            }

            if ( prefix == null || prefix.length() == 0 ) {
                List<Scholar> list;
                synchronized ( mLock ) {
                    list = new ArrayList<Scholar>( mOriginalValues );
                }
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString();

                List<Scholar> values;
                synchronized ( mLock ) {
                    values = new ArrayList<Scholar>( mOriginalValues );
                }

                //final int count = values.size();
                final List<Scholar> newValues = new ArrayList<Scholar>();

                for ( final Scholar scholar : values ) {
                    final String valueText = scholar.getName();

                    // First match against the whole, non-splitted value
                    if ( valueText.startsWith( prefixString ) ) {
                        newValues.add( scholar );
                    } else {
                        final String[] words = valueText.split( " " );
                        //final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with
                        // space(s)
                        for ( String word : words ) {
                            if ( word.startsWith( prefixString ) ) {
                                newValues.add( scholar );
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(
                CharSequence constraint,
                FilterResults results )
        {
            // type unchecked warning. it's safe in this case!
            mScholars = (List<Scholar>) results.values;
            if ( results.count > 0 ) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }*/

	@Override
	public Filter getFilter()
	{
		if ( mFilter == null ) {
			mFilter = new Utils.ArrayFilter<Scholar>( this, mScholars );
		}
		return mFilter;
	}
}
