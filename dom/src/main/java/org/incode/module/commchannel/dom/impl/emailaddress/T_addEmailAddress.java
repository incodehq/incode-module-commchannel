/*
 *
Copyright 2015 incode.org
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
package org.incode.module.commchannel.dom.impl.emailaddress;

import java.util.Collection;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Optionality;
import org.apache.isis.applib.annotation.Parameter;
import org.apache.isis.applib.annotation.ParameterLayout;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.incode.module.commchannel.dom.CommChannelModule;
import org.incode.module.commchannel.dom.impl.purpose.CommunicationChannelPurposeService;
import org.incode.module.commchannel.dom.impl.type.CommunicationChannelType;

public abstract class T_addEmailAddress<T> {

    //region > constructor
    private final T communicationChannelOwner;
    public T_addEmailAddress(final T communicationChannelOwner) {
        this.communicationChannelOwner = communicationChannelOwner;
    }
    @Programmatic
    public T getCommunicationChannelOwner() {
        return communicationChannelOwner;
    }
    //endregion

    //region > $$

    public static class DomainEvent extends CommChannelModule.ActionDomainEvent
                                            <T_addEmailAddress> { }
    @Action(
            domainEvent = DomainEvent.class,
            semantics = SemanticsOf.NON_IDEMPOTENT
    )
    @ActionLayout(
            named = "Email",
            cssClassFa = "fa-plus",
            contributed = Contributed.AS_ACTION
    )
    @MemberOrder(name = "CommunicationChannels", sequence = "2")
    public Object $$(
            @Parameter(
                    regexPattern = CommChannelModule.Regex.EMAIL_ADDRESS,
                    maxLength = CommChannelModule.JdoColumnLength.EMAIL_ADDRESS
            )
            @ParameterLayout(named = "Email Address")
            final String email,
            @Parameter(maxLength = CommChannelModule.JdoColumnLength.PURPOSE, optionality = Optionality.MANDATORY)
            @ParameterLayout(named = "Purpose")
            final String purpose,
            @Parameter(optionality = Optionality.OPTIONAL)
            @ParameterLayout(named = "Notes", multiLine = 10)
            final String notes) {
        emailAddressRepository.newEmail(this.communicationChannelOwner, email, purpose, notes);
        return this.communicationChannelOwner;
    }

    public Collection<String> choices1$$() {
        return communicationChannelPurposeService.purposesFor(CommunicationChannelType.EMAIL_ADDRESS,
                this.communicationChannelOwner);
    }

    public String default1$$() {
        final Collection<String> purposes = choices1$$();
        return purposes.isEmpty()? null : purposes.iterator().next();
    }

    //endregion

    //region > injected services
    @Inject
    CommunicationChannelPurposeService communicationChannelPurposeService;
    @Inject
    EmailAddressRepository emailAddressRepository;
    //endregion


}
