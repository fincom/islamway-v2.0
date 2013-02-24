package com.symbyo.islamway.service.factories;

import android.content.Context;
import com.symbyo.islamway.domain.Section;
import com.symbyo.islamway.service.parsers.CollectionParser;
import com.symbyo.islamway.service.parsers.Parser;
import com.symbyo.islamway.service.processors.CollectionsProcessor;
import com.symbyo.islamway.service.processors.Processor;
import com.symbyo.islamway.service.restclients.RestClient;
import org.eclipse.jdt.annotation.NonNull;

/**
 * @author kdehairy
 * @since 2/20/13
 */
public class CollectionResourceFactory extends ResourceFactory {

    protected final Section mSection;

    public CollectionResourceFactory(
            String url_format, RestClient.HTTPMethod method, Section section )
    {
        super( url_format, method );
        mSection = section;
    }

    @Override
    public RestClient createRestClient()
    {
        return new RestClient( mUrlFormat, mHTTPMethod );
    }

    @Override
    public Parser createParser()
    {
        return new CollectionParser();
    }

    @Override
    public Processor createProcessor( Context context )
    {
        return new CollectionsProcessor( context );
    }
}
