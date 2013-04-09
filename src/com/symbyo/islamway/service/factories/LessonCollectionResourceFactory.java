package com.symbyo.islamway.service.factories;

import android.content.Context;
import com.symbyo.islamway.service.processors.LessonCollectionProcessor;
import com.symbyo.islamway.service.processors.Processor;
import com.symbyo.islamway.service.restclients.RestClient;
import org.eclipse.jdt.annotation.NonNull;

/**
 * @author kdehairy
 * @since 4/9/13
 */
public class LessonCollectionResourceFactory extends CollectionResourceFactory {


	public LessonCollectionResourceFactory( String url_format,
											RestClient.HTTPMethod method,
											int resource_id )
	{
		super( url_format, method, resource_id );
	}

	@Override
	public Processor createProcessor( @NonNull Context context )
	{
		return new LessonCollectionProcessor( context, mResourceId );
	}
}
