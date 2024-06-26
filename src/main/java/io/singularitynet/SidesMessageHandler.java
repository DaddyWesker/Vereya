// --------------------------------------------------------------------------------------------------
//  Copyright (c) 2016 Microsoft Corporation
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
//  associated documentation files (the "Software"), to deal in the Software without restriction,
//  including without limitation the rights to use, copy, modify, merge, publish, distribute,
//  sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in all copies or
//  substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
//  NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
//  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
//  DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// --------------------------------------------------------------------------------------------------

package io.singularitynet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import java.util.*;

import org.jetbrains.annotations.Nullable;


/** Handler for messages from the server to the clients. Register with this to receive specific messages.
 */
public class SidesMessageHandler
{
    public static SidesMessageHandler server2client = new SidesMessageHandler();
    public static SidesMessageHandler client2server = new SidesMessageHandler();

    public interface IMessage {};

    private static Map<VereyaMessageType, List<IVereyaMessageListener>> listeners = new HashMap<VereyaMessageType, List<IVereyaMessageListener>>();

    public SidesMessageHandler() {}

    public boolean registerForMessage(IVereyaMessageListener listener, VereyaMessageType messageType)
    {
        synchronized (listeners) {
            if (!listeners.containsKey(messageType))
                listeners.put(messageType, new ArrayList<IVereyaMessageListener>());

            if (listeners.get(messageType).contains(listener))
                return false;    // Already registered.

            listeners.get(messageType).add(listener);
        }
        return true;
    }

    public boolean deregisterForMessage(IVereyaMessageListener listener, VereyaMessageType messageType)
    {
        synchronized (listeners) {
            if (!listeners.containsKey(messageType)) {
                return false;    // Not registered.
            }

            return listeners.get(messageType).remove(listener);    // Will return false if not present.
        }
    }

    public static void onMessage(MessagePayload payload, ServerPlayNetworking.Context context) {
        final VereyaMessage message = payload.msg();
        final List<IVereyaMessageListener> interestedParties = getMessageListeners(message);
        if (interestedParties == null) return;
        context.server().execute(() -> {
            for (IVereyaMessageListener l : interestedParties)
            {
                // If the message's uid is set (ie non-zero), then use it to ensure that only the matching listener receives this message.
                // Otherwise, let all listeners who are interested get a look.
                // if (message.uid == 0 || System.identityHashCode(l) == message.uid)
                //    l.onMessage(message.messageType,  message.data);
                l.onMessage(message.getMessageType(), message.getData(), context.player());
            }
        });
    }

    public void onMessage(MessagePayload payload, ClientPlayNetworking.Context context) {
        final VereyaMessage message = payload.msg();
        final List<IVereyaMessageListener> interestedParties = getMessageListeners(message);
        if (interestedParties == null) return;
        context.client().execute(() -> {
            for (IVereyaMessageListener l : interestedParties)
            {
                // If the message's uid is set (ie non-zero), then use it to ensure that only the matching listener receives this message.
                // Otherwise, let all listeners who are interested get a look.
                // if (message.uid == 0 || System.identityHashCode(l) == message.uid)
                //    l.onMessage(message.messageType,  message.data);
                l.onMessage(message.getMessageType(), message.getData());
            }
        });
    }

    @Nullable
    private static List<IVereyaMessageListener> getMessageListeners(VereyaMessage message) {
        List<IVereyaMessageListener> interestedParties;
        synchronized (listeners) {
            interestedParties = listeners.get(message.getMessageType());
            if (interestedParties == null) {
                return null;
            }
            // avoid raises by copy
            interestedParties = new ArrayList<>(interestedParties);
        }
        return interestedParties;
    }
}