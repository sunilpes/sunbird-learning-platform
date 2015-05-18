/*
 * Copyright (c) 2013-2014 Canopus Consulting. All rights reserved.
 *
 * This code is intellectual property of Canopus Consulting. The intellectual and technical
 * concepts contained herein may be covered by patents, patents in process, and are protected
 * by trade secret or copyright law. Any unauthorized use of this code without prior approval
 * from Canopus Consulting is prohibited.
 */

/**
 * Script to simulate Telemetry data
 *
 * @author Santhosh
 */
var faker = require('faker');
var fs = require('fs');
require('date-format-lite');
faker.locale = 'en_IND';

function getRandomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function addMinutes(minutes) {
    baseDate = new Date(baseDate.getTime() + minutes * 60000);
}
/**
 * Steps to generate the telemetry data
 * Generate 100 devices with differation locations
 * For each device
 * 	Create 100 users
 * 	 For each user
 * 	   Genie signup
 * 	   Genie start
 * 	   Genie Events
 * 	   Game Events
 * 	   Genie End
 */
var sampleLatLong = ['12.9667,77.5667','12.3000,76.6500','12.8700,74.8800','17.3700,78.4800'];
var events = [];
var st = new Date();
st.setDate(st.getDate() - 15);
var et = new Date();
et.setDate(et.getDate() - 17);
var baseDate = faker.date.between(st, et);

function generate(deviceSize, studentDeviceRatio, fileName) {
	logFile = fileName;
	for(var i=0; i < deviceSize; i++) {
		generateDevice(i, studentDeviceRatio);
	}
	fs.writeFile(logFile, events.join('\n'));
}

function appendEvent(args) {
	addMinutes(args.tmin);
	events.push(JSON.stringify({
		"eid": args.eventId, // unique event ID
		"ts": baseDate.format("YYYY-MM-DD'T'hh:mm:ss'+0530'"),
		"ver": "1.0",
		"gdata": {
		 	"id": "genie.android", // genie id since this is generated by genie
		 	"ver": "1.0" // genie app release version number
		},
		"sid": args.sid,
		"uid": args.uid,
		"did": args.did,
		"edata": {
			"eks": args.eksData
		}
	}));
}

function generateDevice(index, studentDeviceRatio) {
	var did = faker.random.uuid();
	var deviceLoc = sampleLatLong[getRandomInt(0,3)];
	var dspec = {// device specs
       "dname": "MYS_GOVT_HS_001",
       "dlocname": "Mysore Govt High School",
       "os": "Android 4.5",
       "make": "Micromax A80", // device make and model
       "mem": 1000, // total mem in MB
       "idisk": 8, // total internal disk in GB
       "edisk": 32, // total external disk (card) in GB
       "scrn": 4.5, // in inches
       "camera": "13,1.3", // primary and secondary camera
       "cpu": "2.7 GHz Qualcomm Snapdragon 805 Quad Core",
       "sims": 2, // number of sim cards
       "cap": ["GPS","BT","WIFI","3G","ACCEL"] // capabilities enums
    }
    var t1 = baseDate.getTime();
	appendEvent({eventId: 'GE_GENIE_START', tmin: 0, did: did, eksData: {'dspec': dspec, 'loc': deviceLoc}});
	for(var i=0; i < studentDeviceRatio; i++) {
		generateUser(i, did, deviceLoc);
	}
	var t2 = baseDate.getTime();
	appendEvent({eventId: 'GE_GENIE_END', tmin: getRandomInt(1, 2), did: did, eksData: {'length': (t2-t1)/1000}});
}

function generateUser(index, did, deviceLoc) {
	var uid = faker.random.uuid();
	var uname = faker.internet.userName();
	appendEvent({eventId: 'GE_SIGNUP', tmin: getRandomInt(1, 10), did: did, eksData: {uid: uid, ueksid: uname, utype: 'CHILD'}});
	var randomSessions = getRandomInt(1, 4);
	for(var i=0; i < randomSessions; i++) {
		generateUserSession(i, did, uid, uname, deviceLoc);
	}
}

function generateUserSession(index, did, uid, uname, deviceLoc) {
	var sid = faker.random.uuid();
	appendEvent({eventId: 'GE_SESSION_START', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {ueksid: uname, loc: deviceLoc}});
	var t1 = baseDate.getTime();
	var randomGameLaunches = getRandomInt(1, 3);
	for(var i=0; i < randomGameLaunches; i++) {
		appendEvent({eventId: 'GE_LAUNCH_GAME', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {gid: 'G:1', err: ''}});
		var t3 = baseDate.getTime();
		generateOEEvents(index, did, uid, sid);
		var t4 = baseDate.getTime();
		appendEvent({eventId: 'GE_GAME_END', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {gid: 'G:1', err: '', length: (t4-t3)/1000}});
	}
	var t2 = baseDate.getTime();
	appendEvent({eventId: 'GE_SESSION_END', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {length: (t2-t1)/1000}});
}

function generateOEEvents(userIdx, did, uid, sid) {
	appendEvent({eventId: 'OE_START', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {}});
	var t1 = baseDate.getTime();
	appendEvent({eventId: 'OE_LEARN', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {
		"topics": [
			{
				"mc": "C:1",
            	"skill": "",
            	"methods": ['PLAY']
           	}
        ]
	}});
	appendEvent({eventId: 'OE_LEARN', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {
		"topics": [
			{
				"mc": "C:1",
            	"skill": "",
            	"methods": ['ANSWER']
           	}
        ]
	}});
	appendEvent({eventId: 'OE_ASSESS', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {
		subj: 'NUM',
		"mc": "C:1",
        "skill": "",
        "qid": "Q_1",
        "qtype": "INFER",
        "qlevel": "MEDIUM",
        "pass": (userIdx % 7 == 0 ? 'YES': 'NO'),
        "score": (userIdx % 7 == 0 ? getRandomInt(1, 4): getRandomInt(5, 10)),
        "maxscore": 10,
        "length": getRandomInt(10, 20),
        "exlength": 13,
        "atmpts": getRandomInt(1, 5),
        "failedatmpts": getRandomInt(0, 2)
	}});
	appendEvent({eventId: 'OE_INTERACT', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {
		type: 'TOUCH',
		id:'id_1'
	}});
	if(userIdx % 17 == 0) {
		appendEvent({eventId: 'OE_INTERRUPT', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {
			type: 'CALL'
		}});
	}
	if(userIdx % 29 == 0) {
		appendEvent({eventId: 'OE_INTERRUPT', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {
			type: 'SWITCH'
		}});
	}
	if(userIdx % 15 == 0) {
		appendEvent({eventId: 'OE_INTERRUPT', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {
			type: 'IDLE'
		}});
	}
	appendEvent({eventId: 'OE_LEARN', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {
		"topics": [
			{
				"mc": "C:2",
            	"skill": "",
            	"methods": ['PLAY']
           	}
        ]
	}});
	appendEvent({eventId: 'OE_LEARN', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {
		"topics": [
			{
				"mc": "C:2",
            	"skill": "",
            	"methods": ['ANSWER']
           	}
        ]
	}});
	appendEvent({eventId: 'OE_ASSESS', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {
		subj: 'NUM',
		"mc": "C:2",
        "skill": "",
        "qid": "Q_2",
        "qtype": "INFER",
        "qlevel": "MEDIUM",
        "pass": (userIdx % 9 == 0 ? 'YES': 'NO'),
        "score": (userIdx % 9 == 0 ? getRandomInt(1, 4): getRandomInt(5, 10)),
        "maxscore": 10,
        "length": getRandomInt(10, 20),
        "exlength": 13,
        "atmpts": getRandomInt(1, 5),
        "failedatmpts": getRandomInt(0, 2)
	}});
	var t2 = baseDate.getTime();
	appendEvent({eventId: 'OE_END', tmin: getRandomInt(1, 10), did: did, uid: uid, sid: sid, eksData: {length: (t2-t1)/1000}});
}

generate(100, 100, 'simulated_data.json');
