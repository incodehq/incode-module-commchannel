/*
 *  Copyright 2015 incode.org
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.incode.module.commchannel.dom.impl.channel;

import javax.inject.Inject;
import javax.jdo.JDOHelper;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.DomainObject;
import org.apache.isis.applib.annotation.DomainObjectLayout;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;

import org.isisaddons.wicket.gmap3.cpt.applib.Locatable;
import org.isisaddons.wicket.gmap3.cpt.applib.Location;

import org.incode.module.commchannel.dom.CommChannelModule;
import org.incode.module.commchannel.dom.impl.emailaddress.EmailAddress;
import org.incode.module.commchannel.dom.api.owner.CommunicationChannelOwner;
import org.incode.module.commchannel.dom.impl.phoneorfax.PhoneOrFaxNumber;
import org.incode.module.commchannel.dom.impl.postaladdress.PostalAddress;
import org.incode.module.commchannel.dom.impl.type.CommunicationChannelType;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a mechanism for communicating with its
 * {@link CommunicationChannelOwner owner}.
 * 
 * <p>
 * This is an abstract entity; concrete subclasses are {@link PostalAddress
 * postal}, {@link PhoneOrFaxNumber phone/fax} and {@link EmailAddress email}.
 */
@javax.jdo.annotations.PersistenceCapable(
        schema = "incodeCommChannel",
        identityType = IdentityType.DATASTORE
)
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@javax.jdo.annotations.DatastoreIdentity(
        strategy = IdGeneratorStrategy.NATIVE,
        column = "id")
@javax.jdo.annotations.Version(
        strategy = VersionStrategy.VERSION_NUMBER,
        column = "version")
@javax.jdo.annotations.Discriminator(
        strategy = DiscriminatorStrategy.CLASS_NAME,
        column = "discriminator")
@javax.jdo.annotations.Queries({
        @javax.jdo.annotations.Query(
                name = "findByReferenceAndType", language = "JDOQL",
                value = "SELECT "
                        + "FROM org.incode.module.commchannel.dom.impl.channel.CommunicationChannel "
                        + "WHERE reference == :reference "
                        + "&& type == :type")
})
@DomainObject(
        editing = Editing.DISABLED,
        objectType = "incodeCommChannel.CommunicationChannel"
)
@DomainObjectLayout(bookmarking = BookmarkPolicy.AS_CHILD)
public abstract class CommunicationChannel<T extends CommunicationChannel<T>> implements Comparable<CommunicationChannel>,
        Locatable {

    //region > injected services 
    @Inject
    protected DomainObjectContainer container;
    //endregion

    //region > events
    public static abstract class PropertyDomainEvent<S,T>
            extends CommChannelModule.PropertyDomainEvent<S, T> {}

    public static abstract class CollectionDomainEvent<S,T>
            extends CommChannelModule.CollectionDomainEvent<S, T> {}

    public static abstract class ActionDomainEvent<S>
            extends CommChannelModule.ActionDomainEvent<S> { }
    //endregion

    //region > iconName
    public String iconName() {
        return getType().title().replace(" ", "");
    }
    //endregion

    //region > getId (programmatic)
    @Programmatic
    public String getId() {
        Object objectId = JDOHelper.getObjectId(this);
        if(objectId == null) {
            return "";
        }
        String objectIdStr = objectId.toString();
        final String id = objectIdStr.split("\\[OID\\]")[0];
        return id;
    }
    //endregion

    public static class NameEvent extends PropertyDomainEvent<CommunicationChannel,String> {}
    @Property(
            domainEvent = NameEvent.class,
            hidden = Where.OBJECT_FORMS
    )
    public String getName() {
        return container.titleOf(this);
    }


    public static class TypeDomainEvent extends PropertyDomainEvent<CommunicationChannel,CommunicationChannelType> { }
    @Getter @Setter
    @Column(allowsNull = "false", length = CommChannelModule.JdoColumnLength.TYPE_ENUM)
    @Property(
            domainEvent = TypeDomainEvent.class,
            hidden = Where.EVERYWHERE
    )
    private CommunicationChannelType type;


    public static class DescriptionDomainEvent extends PropertyDomainEvent<CommunicationChannel,String> { }
    @Getter @Setter
    @Column(length = org.apache.isis.objectstore.jdo.applib.service.JdoColumnLength.DESCRIPTION)
    @Property(
            domainEvent = DescriptionDomainEvent.class,
            editing = Editing.DISABLED,
            optionality = Optionality.OPTIONAL
    )
    private String description;


    public static class NotesDomainEvent extends PropertyDomainEvent<CommunicationChannel,String> { }
    @Getter @Setter
    @javax.jdo.annotations.Column(allowsNull="true", jdbcType="CLOB")
    @Property(
            domainEvent = NotesDomainEvent.class,
            editing = Editing.DISABLED,
            optionality = Optionality.OPTIONAL
    )
    @PropertyLayout(multiLine = 10)
    private String notes;


    //region > Locatable API

    /**
     * Default implementation just returns <tt>null</tt>
     *
     * <p>
     *     It's necessary for {@link CommunicationChannel} to implement this in order that the gmap3 wicket component
     *     will return any collection of {@link CommunicationChannel}s on a map.
     * </p>
     */
    @Programmatic
    @Override
    public Location getLocation() {
        return null;
    }
    //endregion

    //region > toString, compareTo
    public int compareTo(final CommunicationChannel other) {
        return ObjectContracts.compare(this, other, "type", "id");
    }
    public String toString() {
        return ObjectContracts.toString(this, "type", "id");
    }
    //endregion


}
