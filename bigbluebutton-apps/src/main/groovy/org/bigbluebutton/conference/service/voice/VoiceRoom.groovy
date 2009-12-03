/*
 * BigBlueButton - http://www.bigbluebutton.org
 * 
 * Copyright (c) 2008-2009 by respective authors (see below). All rights reserved.
 * 
 * BigBlueButton is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License as published by the Free Software 
 * Foundation; either version 3 of the License, or (at your option) any later 
 * version. 
 * 
 * BigBlueButton is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along 
 * with BigBlueButton; if not, If not, see <http://www.gnu.org/licenses/>.
 *
 * $Id: $
 */

package org.bigbluebutton.conference.service.voice

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import net.jcip.annotations.ThreadSafe
/**
 * Contains information about a Room. 
 */
@ThreadSafe
public class VoiceRoom {
	private static Logger log = Red5LoggerFactory.getLogger( VoiceRoom.class, "bigbluebutton" )
	
	private final String name
	private final Map<String, IVoiceRoomListener> listeners
	private final Map<String, HashMap> participants
	
	private String conference
	
	public VoiceRoom(String name) {
		this.name = name
		listeners   = new ConcurrentHashMap<String, IVoiceRoomListener>()
		participants = new ConcurrentHashMap<String, HashMap>()
	}
	
	public String getName() {
		return name
	}
	
	public void setConference(String c) {
		conference = c
	}
	
	public String getConference() {
		return conference
	}
	
	public void addRoomListener(IVoiceRoomListener listener) {
		if (! listeners.containsKey(listener.getName())) {
			log.debug("adding room listener")
			listeners.put(listener.getName(), listener)			
		}
	}
	
	public void removeRoomListener(IVoiceRoomListener listener) {
		log.debug("removing room listener")
		listeners.remove(listener)		
	}
	
	def joined(participant, name, muted, talking){
		Map p = new HashMap()
		p.put('participant', participant)
		p.put('name', name)
		p.put('muted', muted)
		p.put('talking', talking)
		participants.put(participant, p)
		
		for (Iterator iter = listeners.values().iterator(); iter.hasNext();) {
			log.debug("calling on listener")
			IVoiceRoomListener listener = (IVoiceRoomListener) iter.next()
			log.debug("calling joined on listener ${listener.getName()}")
			listener.joined(participant, name, muted, talking)
		}
	}
	
	def left(participant){
		Map p = (HashMap) participants.remove(participant)
		log.debug "User left $p"
		
		for (Iterator iter = listeners.values().iterator(); iter.hasNext();) {
			log.debug("calling on listener")
			IVoiceRoomListener listener = (IVoiceRoomListener) iter.next()
			log.debug("calling left on listener ${listener.getName()}")
			listener.left(participant)
		}
	}
	
	def mute(participant, mute){
		log.debug("mute: $participant $mute")
		Map p = (HashMap) participants.get(participant)
		p.put('muted', mute)
		log.debug "Muted participant $p"
		
		for (Iterator iter = listeners.values().iterator(); iter.hasNext();) {
			log.debug("calling on listener")
			IVoiceRoomListener listener = (IVoiceRoomListener) iter.next()
			log.debug("calling mute on listener ${listener.getName()}")
			listener.mute(participant, mute)
		}
	}
	

	def talk(participant, talk){
		log.debug("talk: $participant $talk")
		Map p = (HashMap) participants.get(participant)
		p.put('talking', talk)
		for (Iterator iter = listeners.values().iterator(); iter.hasNext();) {
			log.debug("calling on listener")
			IVoiceRoomListener listener = (IVoiceRoomListener) iter.next()
			log.debug("calling talk on listener ${listener.getName()}")
			listener.talk(participant, talk)
		}
	}	
	
	def participants() {
		return new HashMap(participants)
	}
}