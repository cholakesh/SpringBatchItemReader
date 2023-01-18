package com.itemreader.config;

import java.io.File;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import com.itemreader.model.StudentCsv;
import com.itemreader.model.StudentJdbc;
import com.itemreader.model.StudentJson;
import com.itemreader.model.StudentResponse;
import com.itemreader.model.StudentXml;
import com.itemreader.processor.FirstItemProcessor;
import com.itemreader.reader.FirstItemReader;
import com.itemreader.service.RestApiItemReader;
import com.itemreader.writer.FirstItemWriter;

@Configuration
public class SampleJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private FirstItemReader firstItemReader;

    @Autowired
    private FirstItemProcessor firstItemProcessor;

    @Autowired
    private FirstItemWriter firstItemWriter;

    // @Autowired
    // private DataSource dataSource;

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.universitydatasource")
    public DataSource universityDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Autowired
    private RestApiItemReader restApiItemReader;

    @Bean
    public Job chunkJob() {
        return jobBuilderFactory.get("Chunk Job")
                .incrementer(new RunIdIncrementer())
                .start(firstChunkStep())
                .build();
    }

    private Step firstChunkStep() {
        return stepBuilderFactory.get("First Chunk Step")
                // .<StudentCsv, StudentCsv>chunk(3)
                .<StudentResponse, StudentResponse>chunk(3)
                // .reader(flatFileItemReader(null))
                // .reader(jsonItemReader(null))
                // .reader(staxEventItemReader(null))
                // .reader(jdbcCursorItemReader())
                .reader(itemReaderAdapter())
                // .processor(firstItemProcessor)
                .writer(firstItemWriter)
                .build();
    }

    @StepScope
    @Bean // as we are using this @Value annotation this flatFileItemReader needs to be in
          // context
    public FlatFileItemReader<StudentCsv> flatFileItemReader(
            @Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
        FlatFileItemReader<StudentCsv> flatFileItemReader = new FlatFileItemReader<StudentCsv>();

        flatFileItemReader.setResource(fileSystemResource);

        flatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer() {
                    {
                        setNames("ID", "First Name", "Last Name", "Email"); // order shouldn't change . Should be same
                                                                            // as CSV file
                    }
                });

                setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {
                    {
                        setTargetType(StudentCsv.class);
                    }
                });
            }
        });

        flatFileItemReader.setLinesToSkip(1);
        return flatFileItemReader;
    }

    @StepScope
    @Bean
    public JsonItemReader<StudentJson> jsonItemReader(
            @Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
        JsonItemReader<StudentJson> jsonItemReader = new JsonItemReader<>();

        jsonItemReader.setResource(fileSystemResource);

        jsonItemReader.setJsonObjectReader(new JacksonJsonObjectReader<>(StudentJson.class));

        jsonItemReader.setMaxItemCount(8); // this is for setting how many items to read from json file.
        jsonItemReader.setCurrentItemCount(2); // this is used to ignore first 2 objects and start from 3. default value
        // is 0.
        return jsonItemReader;
    }

    @StepScope
    @Bean
    public StaxEventItemReader<StudentXml> staxEventItemReader(
            @Value("#{jobParameters['inputFile']}") FileSystemResource fileSystemResource) {
        StaxEventItemReader<StudentXml> staxEventItemReader = new StaxEventItemReader<>();

        staxEventItemReader.setResource(fileSystemResource);

        staxEventItemReader.setFragmentRootElementName("student");

        staxEventItemReader.setUnmarshaller(new Jaxb2Marshaller() {
            {
                setClassesToBeBound(StudentXml.class);
            }
        });

        return staxEventItemReader;
    }

    public JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader() {
        JdbcCursorItemReader<StudentJdbc> jdbcCursorItemReader = new JdbcCursorItemReader<>();

        jdbcCursorItemReader.setDataSource(universityDataSource());

        jdbcCursorItemReader
                .setSql("select Id as id, First_Name, Last_Name as lastName, Email as email from students");

        jdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<StudentJdbc>() {
            {
                setMappedClass(StudentJdbc.class);
            }
        });

        jdbcCursorItemReader.setCurrentItemCount(2);
        jdbcCursorItemReader.setMaxItemCount(8);
        return jdbcCursorItemReader;
    }

    public ItemReaderAdapter<StudentResponse> itemReaderAdapter() {
        ItemReaderAdapter<StudentResponse> itemReaderAdapter = new ItemReaderAdapter<StudentResponse>();

        itemReaderAdapter.setTargetObject(restApiItemReader);
        itemReaderAdapter.setTargetMethod("indiviualData");

        itemReaderAdapter.setArguments(new Object[] { 1L, "Cholakesh" });

        return itemReaderAdapter;
    }
}
