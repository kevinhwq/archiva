package org.apache.archiva.rest.api.services;
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

import org.apache.archiva.admin.repository.RepositoryAdminException;
import org.apache.archiva.rest.api.model.FileType;
import org.apache.archiva.rest.api.model.LegacyArtifactPath;
import org.apache.archiva.rest.api.model.RepositoryScanning;
import org.apache.archiva.security.common.ArchivaRoleConstants;
import org.codehaus.plexus.redback.authorization.RedbackAuthorization;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * @author Olivier Lamy
 * @since 1.4
 */
@Path( "/archivaAdministrationService/" )
public interface ArchivaAdministrationService
{
    @Path( "getLegacyArtifactPaths" )
    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    List<LegacyArtifactPath> getLegacyArtifactPaths()
        throws RepositoryAdminException;

    @Path( "addLegacyArtifactPath" )
    @POST
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    void addLegacyArtifactPath( LegacyArtifactPath legacyArtifactPath )
        throws RepositoryAdminException;

    @Path( "deleteLegacyArtifactPath" )
    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    Boolean deleteLegacyArtifactPath( @QueryParam( "path" ) String path )
        throws RepositoryAdminException;

    @Path( "getRepositoryScanning" )
    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    RepositoryScanning getRepositoryScanning()
        throws RepositoryAdminException;

    @Path( "updateRepositoryScanning" )
    @POST
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    void updateRepositoryScanning( RepositoryScanning repositoryScanning )
        throws RepositoryAdminException;

    @Path( "addFileTypePattern" )
    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    Boolean addFileTypePattern( @QueryParam( "fileTypeId" ) String fileTypeId, @QueryParam( "pattern" ) String pattern )
        throws RepositoryAdminException;

    @Path( "removeFileTypePattern" )
    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    Boolean removeFileTypePattern( @QueryParam( "fileTypeId" ) String fileTypeId,
                                   @QueryParam( "pattern" ) String pattern )
        throws RepositoryAdminException;

    @Path( "getFileType" )
    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    FileType getFileType( String fileTypeId )
        throws RepositoryAdminException;

    @Path( "addFileType" )
    @POST
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    void addFileType( FileType fileType )
        throws RepositoryAdminException;

    @Path( "removeFileType" )
    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    Boolean removeFileType( @QueryParam( "fileTypeId" ) String fileTypeId )
        throws RepositoryAdminException;

    @Path( "addKnownContentConsumer" )
    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    Boolean addKnownContentConsumer( @QueryParam( "knownContentConsumer" ) String knownContentConsumer )
        throws RepositoryAdminException;

    @Path( "setKnownContentConsumers" )
    @POST
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    void setKnownContentConsumers( List<String> knownContentConsumers )
        throws RepositoryAdminException;


    @Path( "removeKnownContentConsumer" )
    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    Boolean removeKnownContentConsumer( @QueryParam( "knownContentConsumer" )  String knownContentConsumer )
        throws RepositoryAdminException;

    @Path( "addInvalidContentConsumer" )
    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    Boolean addInvalidContentConsumer(@QueryParam( "invalidContentConsumer" ) String invalidContentConsumer )
        throws RepositoryAdminException;

    @Path( "setInvalidContentConsumers" )
    @POST
    @Consumes( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    void setInvalidContentConsumers( List<String> invalidContentConsumers )
        throws RepositoryAdminException;

    @Path( "removeInvalidContentConsumer" )
    @GET
    @Produces( { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_PLAIN } )
    @RedbackAuthorization( permission = ArchivaRoleConstants.OPERATION_MANAGE_CONFIGURATION )
    Boolean removeInvalidContentConsumer(@QueryParam( "invalidContentConsumer" )  String invalidContentConsumer )
        throws RepositoryAdminException;
}
