package io.github.zoooohs.dynamicdatasource.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JpaRepositoryScanner {
    public List<Class<?>> findJpaRepositoryInterfaces(String basePackage) throws IOException, ClassNotFoundException {
        List<Class<?>> jpaRepositoryInterfaces = new ArrayList<>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(StringUtils.hasText(basePackage) ?
                        basePackage : ClassUtils.getPackageName(getClass())) + "/**/*.class";
        PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
        AssignableTypeFilter assignableTypeFilter = new AssignableTypeFilter(JpaRepository.class);

        for (Resource resource : resources) {
            if (resource.isReadable()) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                if (assignableTypeFilter.match(metadataReader, metadataReaderFactory)) {
                    jpaRepositoryInterfaces.add(Class.forName(metadataReader.getClassMetadata().getClassName()));
                }
            }
        }
        return jpaRepositoryInterfaces;
    }
}
