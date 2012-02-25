package org.apache.archiva.rest.services;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.archiva.metadata.model.ProjectVersionMetadata;
import org.apache.archiva.metadata.repository.MetadataResolutionException;
import org.apache.archiva.metadata.repository.MetadataResolver;
import org.apache.archiva.metadata.repository.RepositorySession;
import org.apache.archiva.metadata.repository.storage.maven2.MavenProjectFacet;
import org.apache.archiva.rest.api.model.BrowseResult;
import org.apache.archiva.rest.api.model.BrowseResultEntry;
import org.apache.archiva.rest.api.model.VersionsList;
import org.apache.archiva.rest.api.services.ArchivaRestServiceException;
import org.apache.archiva.rest.api.services.BrowseService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Olivier Lamy
 * @since 1.4-M3
 */
@Service( "browseService#rest" )
public class DefaultBrowseService
    extends AbstractRestService
    implements BrowseService
{

    public BrowseResult getRootGroups()
        throws ArchivaRestServiceException
    {
        List<String> selectedRepos = getObservableRepos();
        if ( CollectionUtils.isEmpty( selectedRepos ) )
        {
            // FIXME 403 ???
            return new BrowseResult();
        }

        Set<String> namespaces = new LinkedHashSet<String>();

        // TODO: this logic should be optional, particularly remembering we want to keep this code simple
        //       it is located here to avoid the content repository implementation needing to do too much for what
        //       is essentially presentation code
        Set<String> namespacesToCollapse;
        RepositorySession repositorySession = repositorySessionFactory.createSession();
        try
        {
            MetadataResolver metadataResolver = repositorySession.getResolver();
            namespacesToCollapse = new LinkedHashSet<String>();
            for ( String repoId : selectedRepos )
            {
                namespacesToCollapse.addAll( metadataResolver.resolveRootNamespaces( repositorySession, repoId ) );
            }
            for ( String n : namespacesToCollapse )
            {
                // TODO: check performance of this
                namespaces.add( collapseNamespaces( repositorySession, metadataResolver, selectedRepos, n ) );
            }
        }
        catch ( MetadataResolutionException e )
        {
            throw new ArchivaRestServiceException( e.getMessage(),
                                                   Response.Status.INTERNAL_SERVER_ERROR.getStatusCode() );
        }
        finally
        {
            repositorySession.close();
        }

        List<BrowseResultEntry> browseGroupResultEntries = new ArrayList<BrowseResultEntry>( namespaces.size() );
        for ( String namespace : namespaces )
        {
            browseGroupResultEntries.add( new BrowseResultEntry( namespace, false ) );
        }

        Collections.sort( browseGroupResultEntries );
        return new BrowseResult( browseGroupResultEntries );
    }

    public BrowseResult browseGroupId( String groupId )
        throws ArchivaRestServiceException
    {

        List<String> selectedRepos = getObservableRepos();
        if ( CollectionUtils.isEmpty( selectedRepos ) )
        {
            // FIXME 403 ???
            return new BrowseResult();
        }

        Set<String> projects = new LinkedHashSet<String>();

        RepositorySession repositorySession = repositorySessionFactory.createSession();
        Set<String> namespaces;
        try
        {
            MetadataResolver metadataResolver = repositorySession.getResolver();

            Set<String> namespacesToCollapse = new LinkedHashSet<String>();
            for ( String repoId : selectedRepos )
            {
                namespacesToCollapse.addAll( metadataResolver.resolveNamespaces( repositorySession, repoId, groupId ) );

                projects.addAll( metadataResolver.resolveProjects( repositorySession, repoId, groupId ) );
            }

            // TODO: this logic should be optional, particularly remembering we want to keep this code simple
            // it is located here to avoid the content repository implementation needing to do too much for what
            // is essentially presentation code
            namespaces = new LinkedHashSet<String>();
            for ( String n : namespacesToCollapse )
            {
                // TODO: check performance of this
                namespaces.add(
                    collapseNamespaces( repositorySession, metadataResolver, selectedRepos, groupId + "." + n ) );
            }
        }
        catch ( MetadataResolutionException e )
        {
            throw new ArchivaRestServiceException( e.getMessage(),
                                                   Response.Status.INTERNAL_SERVER_ERROR.getStatusCode() );
        }
        finally
        {
            repositorySession.close();
        }
        List<BrowseResultEntry> browseGroupResultEntries =
            new ArrayList<BrowseResultEntry>( namespaces.size() + projects.size() );
        for ( String namespace : namespaces )
        {
            browseGroupResultEntries.add( new BrowseResultEntry( namespace, false ) );
        }
        for ( String project : projects )
        {
            browseGroupResultEntries.add( new BrowseResultEntry( groupId + '.' + project, true ) );
        }
        Collections.sort( browseGroupResultEntries );
        return new BrowseResult( browseGroupResultEntries );

    }

    public VersionsList getVersionsList( String groupId, String artifactId )
        throws ArchivaRestServiceException
    {
        List<String> selectedRepos = getObservableRepos();
        if ( CollectionUtils.isEmpty( selectedRepos ) )
        {
            // FIXME 403 ???
            return new VersionsList();
        }

        try
        {
            return new VersionsList( new ArrayList<String>( getVersions( selectedRepos, groupId, artifactId ) ) );
        }
        catch ( MetadataResolutionException e )
        {
            throw new ArchivaRestServiceException( e.getMessage(),
                                                   Response.Status.INTERNAL_SERVER_ERROR.getStatusCode() );
        }

    }

    private Set<String> getVersions( List<String> selectedRepos, String groupId, String artifactId )
        throws MetadataResolutionException

    {
        RepositorySession repositorySession = repositorySessionFactory.createSession();
        try
        {
            MetadataResolver metadataResolver = repositorySession.getResolver();

            Set<String> versions = new LinkedHashSet<String>();

            for ( String repoId : selectedRepos )
            {
                versions.addAll(
                    metadataResolver.resolveProjectVersions( repositorySession, repoId, groupId, artifactId ) );
            }

            return versions;
        }
        finally
        {
            repositorySession.close();
        }
    }

    public ProjectVersionMetadata getProjectVersionMetadata( String groupId, String artifactId )
        throws ArchivaRestServiceException
    {

        List<String> selectedRepos = getObservableRepos();

        if ( CollectionUtils.isEmpty( selectedRepos ) )
        {
            // FIXME 403 ???
            return null;
        }

        RepositorySession repositorySession = null;
        try
        {

            Set<String> projectVersions = getVersions( selectedRepos, groupId, artifactId );

            repositorySession = repositorySessionFactory.createSession();

            MetadataResolver metadataResolver = repositorySession.getResolver();

            ProjectVersionMetadata sharedModel = new ProjectVersionMetadata();

            MavenProjectFacet mavenFacet = new MavenProjectFacet();
            mavenFacet.setGroupId( groupId );
            mavenFacet.setArtifactId( artifactId );
            sharedModel.addFacet( mavenFacet );

            boolean isFirstVersion = true;

            for ( String version : projectVersions )
            {
                ProjectVersionMetadata versionMetadata = null;
                for ( String repoId : selectedRepos )
                {
                    if ( versionMetadata == null )
                    {
                        try
                        {
                            versionMetadata =
                                metadataResolver.resolveProjectVersion( repositorySession, repoId, groupId, artifactId,
                                                                        version );
                        }
                        catch ( MetadataResolutionException e )
                        {
                            log.error( "Skipping invalid metadata while compiling shared model for " + groupId + ":"
                                           + artifactId + " in repo " + repoId + ": " + e.getMessage() );
                        }
                    }
                }

                if ( versionMetadata == null )
                {
                    continue;
                }

                if ( isFirstVersion )
                {
                    sharedModel = versionMetadata;
                    sharedModel.setId( null );
                }
                else
                {
                    MavenProjectFacet versionMetadataMavenFacet =
                        (MavenProjectFacet) versionMetadata.getFacet( MavenProjectFacet.FACET_ID );
                    if ( versionMetadataMavenFacet != null )
                    {
                        if ( mavenFacet.getPackaging() != null && !StringUtils.equalsIgnoreCase(
                            mavenFacet.getPackaging(), versionMetadataMavenFacet.getPackaging() ) )
                        {
                            mavenFacet.setPackaging( null );
                        }
                    }

                    if ( sharedModel.getName() != null && !StringUtils.equalsIgnoreCase( sharedModel.getName(),
                                                                                         versionMetadata.getName() ) )
                    {
                        sharedModel.setName( "" );
                    }

                    if ( sharedModel.getDescription() != null && !StringUtils.equalsIgnoreCase(
                        sharedModel.getDescription(), versionMetadata.getDescription() ) )
                    {
                        sharedModel.setDescription( null );
                    }

                    if ( sharedModel.getIssueManagement() != null && versionMetadata.getIssueManagement() != null
                        && !StringUtils.equalsIgnoreCase( sharedModel.getIssueManagement().getUrl(),
                                                          versionMetadata.getIssueManagement().getUrl() ) )
                    {
                        sharedModel.setIssueManagement( null );
                    }

                    if ( sharedModel.getCiManagement() != null && versionMetadata.getCiManagement() != null
                        && !StringUtils.equalsIgnoreCase( sharedModel.getCiManagement().getUrl(),
                                                          versionMetadata.getCiManagement().getUrl() ) )
                    {
                        sharedModel.setCiManagement( null );
                    }

                    if ( sharedModel.getOrganization() != null && versionMetadata.getOrganization() != null
                        && !StringUtils.equalsIgnoreCase( sharedModel.getOrganization().getName(),
                                                          versionMetadata.getOrganization().getName() ) )
                    {
                        sharedModel.setOrganization( null );
                    }

                    if ( sharedModel.getUrl() != null && !StringUtils.equalsIgnoreCase( sharedModel.getUrl(),
                                                                                        versionMetadata.getUrl() ) )
                    {
                        sharedModel.setUrl( null );
                    }
                }

                isFirstVersion = false;
            }
            return sharedModel;
        }
        catch ( MetadataResolutionException e )
        {
            throw new ArchivaRestServiceException( e.getMessage(),
                                                   Response.Status.INTERNAL_SERVER_ERROR.getStatusCode() );
        }
        finally
        {
            if ( repositorySession != null )
            {
                repositorySession.close();
            }
        }
    }

    //---------------------------
    // internals
    //---------------------------

    private List<String> getSortedList( Set<String> set )
    {
        List<String> list = new ArrayList<String>( set );
        Collections.sort( list );
        return list;
    }

    private String collapseNamespaces( RepositorySession repositorySession, MetadataResolver metadataResolver,
                                       Collection<String> repoIds, String n )
        throws MetadataResolutionException
    {
        Set<String> subNamespaces = new LinkedHashSet<String>();
        for ( String repoId : repoIds )
        {
            subNamespaces.addAll( metadataResolver.resolveNamespaces( repositorySession, repoId, n ) );
        }
        if ( subNamespaces.size() != 1 )
        {
            log.debug( "{} is not collapsible as it has sub-namespaces: {}", n, subNamespaces );
            return n;
        }
        else
        {
            for ( String repoId : repoIds )
            {
                Collection<String> projects = metadataResolver.resolveProjects( repositorySession, repoId, n );
                if ( projects != null && !projects.isEmpty() )
                {
                    log.debug( "{} is not collapsible as it has projects", n );
                    return n;
                }
            }
            return collapseNamespaces( repositorySession, metadataResolver, repoIds,
                                       n + "." + subNamespaces.iterator().next() );
        }
    }
}