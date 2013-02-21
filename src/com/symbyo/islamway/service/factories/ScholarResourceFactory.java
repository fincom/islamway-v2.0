package com.symbyo.islamway.service.factories;

import org.eclipse.jdt.annotation.NonNull;

import android.content.Context;

import com.symbyo.islamway.domain.Section;
import com.symbyo.islamway.service.parsers.Parser;
import com.symbyo.islamway.service.parsers.ScholarParser;
import com.symbyo.islamway.service.processors.Processor;
import com.symbyo.islamway.service.processors.ScholarProcessor;
import com.symbyo.islamway.service.restclients.RestClient;

public class ScholarResourceFactory extends ResourceFactory {
    protected final Section mSection;

    /**
     * Initializes a ScholarResourceFactory. The default_section parameter is
     * needed by the ScholarParser.
     *
     * @param url_format      string with the id placeholder matching the format function.
     * @param http_method     http method used.
     * @param default_section the section to which requested scholars belong.
     */
    public ScholarResourceFactory(
            @NonNull String url_format,
            RestClient.HTTPMethod http_method, Section default_section )
    {

        super( url_format, http_method );
        mSection = default_section;
    }

    @SuppressWarnings("null")
    @Override
    public RestClient createRestClient()
    {

        return new RestClient( mUrlFormat, mHTTPMethod );
    }

    @Override
    public Parser createParser()
    {
        return new ScholarParser();
    }

    @Override
    public Processor createProcessor( @NonNull Context context )
    {
        ScholarProcessor processor = new ScholarProcessor( context, mSection );
        processor.setOnPostProcessingListener( mPostProcessingListener );
        return processor;
    }

}
