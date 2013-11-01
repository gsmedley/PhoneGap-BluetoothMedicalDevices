

var cordovaReady = false,
initReady = false,
medicalDevicePlugin,

/*
 * Utility function for plugin initialization
 */
initialize = function() {
    medicalDevicePlugin = new Service();
    medicalDevicePlugin.set({
        nativePlugin: 'MedicalDevicePlugin',
        cordova: cordova
    });


    /*
     * Device ready Cordova hook for plugin initialization
     */
    var eventListener = function() {
        window.document.removeEventListener('deviceready', eventListener, false);
        cordovaReady = true;
        medicalDevicePlugin.emit('cordovaReady');
    };
    window.document.addEventListener('deviceready', eventListener, false);
},

/*
 * Utility method for creating and attaching callback Id's
 */
generateId = function(callback) {
    callback.id = Math.floor(Math.random() * 2000000000);

    return callback;
},

/*
 * Utility method for subclassing
 */
extend = function(Child, Parent) {
    Child.prototype = new Parent();
    Child.prototype.constructor = Child;
    Child.parent = Parent;
};

/*
 * Base class for event emitters
 */
var Emitter = function() {
    this.m_callbackMap = {}; // Map of callbacks searchable by event name
    this.m_events = [];
};

Emitter.prototype.set = function(properties) {
    var memberName, setterName;

    for (var prop in properties) {
        memberName = 'm_' + prop;

        // Check for property on current instance
        if (this[memberName] !== undefined) {
            setterName = 'set_' + prop;

            // Check for setter method on current instance for specified property
            if (this[setterName] !== undefined) {
                this[setterName].call(this, properties[prop]);     // Call setter
            }
            else {
                this[memberName] = properties[prop];    // Set property
            }
        }
    }

    return this;
};
Emitter.prototype.set_events = function(events){
    this.m_events = events;

    for (var i = this.m_events.length - 1; i >= 0; i--) {
        this.m_callbackMap[this.m_events[i]] = [];
    }
};
Emitter.prototype.on = function(event, callback) {
    if (typeof callback === 'function') {
        if(this.m_callbackMap[event]) {
            this.m_callbackMap[event].push(generateId(callback));
        }
        else {
            callback.call(this, 'invalid event: ' + event);
        }
    }

    return this;
};
Emitter.prototype.remove = function(event, callback) {
    if (typeof callback === 'function') {
        if(this.m_callbackMap[event]) {
            var callbackList = this.m_callbackMap[event] || [];

            for (var i = callbackList.length - 1; i >= 0; i--) {
                if (callback.id === callbackList[i].id) {
                    callbackList.splice(i, 1);

                    return this;
                }
            }
        }
        else {
            callback.call(this, 'invalid event: ' + event);
        }
    }

    return this;
};
Emitter.prototype.emit = function(event, error, value) {
    var callbackList = this.m_callbackMap[event] || [];

    for (var i = callbackList.length - 1; i >= 0; i--) {
        callbackList[i].call(this, error, value);
    }

    return this;
};
Emitter.prototype.getEvents = function(callback) {
    if (typeof callback === 'function') {
        callback.call(this, undefined, {
            events: this.m_events
        });
    }

    return this;
};

/*
 * Base plugin class with the ability to call native code through cordova
 */
var CordovaPlugin = function() {
    CordovaPlugin.parent.call(this);

    this.m_cordova = {};
    this.m_nativePlugin = '';
};

extend(CordovaPlugin, Emitter);

CordovaPlugin.prototype.set_nativePlugin = function(name) {
    this.m_nativePlugin = name;
};
CordovaPlugin.prototype.invoke = function(action, args, callback) {
    // If plugin is not initialized, defer invoke call until device plugin is initialized
    if (!initReady) {
        this.on('initialized', (function(action, args, callback) {
            var localThis = this;

            return function() {
                localThis.invoke.call(localThis, action, args, callback);
            };
        }).call(this, action, args, callback));

        return this;
    }

    // Cordova doesn't maintain 'this' context in it's exec(), variables refering to 'this' in the callback will access the wrong object
    // Work around is to use a local reference and set the value of 'this' via callback.call()
    var localThis = this;

    this.m_cordova.exec(
        function(param) {
            callback.call(localThis, undefined, param); // Error is expected to be the first param in callbacks
        },
        function(e) {
            callback.call(localThis, e);
        },
        this.m_nativePlugin,
        action,
        args || []
    );

    return this;
};

/*
 * Javascript interface to medical devices
 */
var Service = function() {
    Service.parent.call(this);

    this.set({
        events: [
            'initialized',
            'enabled',
            'disabled',
            'connection',
            'cordovaReady',
            'discovery',
            'connect'
        ]
    });

    return this;
};

extend(Service, CordovaPlugin);

Service.prototype.initialize = function(callback) {
    // If cordova is not ready, defer initialize call until cordova is ready
    if (!cordovaReady) {
        this.on('cordovaReady', (function(callback) {
            var localThis = this;

            return function() {
                initReady = true;

                localThis.initialize.call(localThis, callback);
            };
        }).call(this, callback));

        return this;
    } else {
        initReady = true;
    }

    this.emit('initialized', undefined, {
        status: true
    });

    if (typeof callback === 'function') {
        callback.call(this, undefined, {
            status: true
        });
    }

    return this;
};
/*
 * Asynchronous actions, emit events
 */
Service.prototype.enable = function(callback) {
    this.invoke(
        'enable',
        [],
        function(e, status) {
            if (e) {
                this.emit('enabled', e);

                if (typeof callback === 'function') {
                    callback.call(this, e);
                }
            }
            else {
                this.emit('enabled', undefined, {
                    status: status
                });

                if (typeof callback === 'function') {
                    callback.call(this, undefined, {
                        status: status
                    });
                }
            }
        }
    );

    return this;
};
Service.prototype.disable = function(callback) {
    this.invoke(
        'disable',
        [],
        function(e, status) {
            if (e) {
                this.emit('disabled', e);

                if (typeof callback === 'function') {
                    callback.call(this, e);
                }
            }
            else {
                this.emit('disabled', undefined, {
                    status: status
                });

                if (typeof callback === 'function') {
                    callback.call(this, undefined, {
                        status: status
                    });
                }
            }
        }
    );

    return this;
};
Service.prototype.discoverable = function(params, callback) {
    var time = 120;

    if(params && isFinite(params.time)){
        time = params.time < 0 ? 0 : params.time > 300 ? 300 : params.time;
    }

    this.invoke(
        'discoverable',
        [time],
        function(e, payload) {
            if (e) {
                this.emit('discovery', e);

                if (typeof callback === 'function') {
                    callback.call(this, e);
                }
            }
            else {
                this.emit('discovery', undefined, {
                    status: payload.status,
                    time: payload.time
                });

                if (typeof callback === 'function') {
                    callback.call(this, undefined, {
                        status: payload.status,
                        time: payload.time
                    });
                }
            }
        }
    );

    return this;
};
Service.prototype.connect = function(params, callback) {
    var obj = {
        time: 120,
        brand: '',
        type: '',
        serial: '',
        num: 0,
        address: '',
        input: []
    };

    if (params) {
        if (typeof params.brand === 'string') {
            obj.brand = params.brand;
        }
        if (typeof params.type === 'string') {
            obj.type = params.type;
        }
        if (typeof params.serial === 'string') {
            obj.serial = params.serial;
        }
        if (isFinite(params.time)) {
            obj.time = params.time;
        }
        if (isFinite(params.num)) {
            obj.num = params.num;
        }
        if (typeof params.address === 'string') {
            obj.address = params.address;
        }
        // Unit test hook for sending in device data
        if (Array.isArray(params.input)) {
            obj.input = params.input;
        }
    }

    this.invoke(
        'connect',
        [obj],
        function(e, payload) {
            if (e) {
                this.emit('connection', e);

                if (typeof callback === 'function') {
                    callback.call(this, e);
                }
            }
            else if (typeof payload === 'boolean') {
                this.emit('connect', undefined, {
                    status: true
                });

                if (typeof callback === 'function') {
                    // This is for the disconnect event, this functions callback should be notified
                    var eventListener = function(e, payload) {
                        if (payload && payload.status === false) {
                            this.remove('connect', eventListener);

                            callback.call(this, undefined, {
                                status: false
                            });
                        }
                    };

                    this.on('connect', eventListener);

                    callback.call(this, undefined, {
                        status: true
                    });
                }
            }
            else {
                this.emit('connection', undefined, payload);

                if (typeof callback === 'function') {
                    callback.call(this, undefined, payload);
                }
            }
        }
    );

    return this;
};

Service.prototype.disconnect = function(params, callback) {
    var obj = {
        brand: '',
    };
    if (params) {
        if (typeof params.brand === 'string') {
            obj.brand = params.brand;
        }
    }

    this.invoke(
        'disconnect',
        [obj],
        function(e/*, payload*/) {
            if (e) {
                this.emit('connect', e);

                if (typeof callback === 'function') {
                    callback.call(this, e);
                }
            }
            else {
                this.emit('connect', undefined, {
                    status: false
                });

                if (typeof callback === 'function') {
                    callback.call(this, undefined, {
                        status: false
                    });
                }
            }
        }
    );

    return this;
};
/*
 * Synchronous status requests
 */
Service.prototype.isEnabled = function(callback) {
    if (typeof callback === 'function') {
        this.invoke(
            'isEnabled',
            [],
            function(e, status) {
                if (e) {
                    callback.call(this, e);
                }
                else {
                    callback.call(this, undefined, {
                        status: status
                    });
                }
            }
        );
    }

    return this;
};
Service.prototype.isSupported = function(callback) {
    if (typeof callback === 'function') {
        this.invoke(
            'isSupported',
            [],
            function(e, status) {
                if (e) {
                    callback.call(this, e);
                }
                else {
                    callback.call(this, undefined, {
                        status: status
                    });
                }
            }
        );
    }

    return this;
};
// TODO: Remove this method, fix tests
Service.prototype.getBonded = function(callback) {
    if (typeof callback === 'function') {
        this.invoke(
            'getBonded',
            [],
            function(e, payload) {
                if (e) {
                    callback.call(this, e);
                }
                else {
                    callback.call(this, undefined, {
                        devices: payload || []
                    });
                }
            }
        );
    }

    return this;
};
Service.prototype.getTypes = function(callback) {
    if (typeof callback === 'function') {
        this.invoke(
            'types',
            [],
            function(e, payload) {
                if (e) {
                    callback.call(this, e);
                }
                else {
                    callback.call(this, undefined, {
                        types: payload || []
                    });
                }
            }
        );
    }

    return this;
};
// TODO: change bonded tests to work with this method, change demo
Service.prototype.getDeviceMetadata = function(params, callback) {
    if (typeof callback === 'function') {
        this.invoke(
            'getMeta',
            [],
            function(e, payload) {
                if (e) {
                    callback.call(this, e);
                }
                else {
                    callback.call(this, undefined, {
                        devices: payload || []
                    });
                }
            }
        );
    }

    return this;
};
Service.prototype.createDevice = function(params, callback) {
    if (typeof callback === 'function') {
        callback.call(this, undefined, new Device(params, this));
    }

    return this;
};

var Device = function(metaData, service) {
    this.service = service;

    this.name = metaData.name || undefined;
    this.address = metaData.address || undefined;
    this.serial = metaData.serial || undefined;
    this.manufacturer = metaData.manufacturer || undefined;
    this.type = metaData.type || undefined;
    this.protocol = metaData.protocol || undefined;

    this.set({
        events: [
            'connection'
        ]
    });
};

extend(Device, Emitter);

Device.prototype.connect = function(params, callback) {
    params.name = this.name;
    params.address = this.address;
    params.serial = this.serial;
    params.manufacturer = this.manufacturer;
    params.type = this.type;
    params.protocol = this.protocol;

    // TODO: move implementation into this function
    this.service.listen(params, callback);

    return this;
};
Device.prototype.disconnect = function(params, callback) {
    params.name = this.name;
    params.address = this.address;
    params.serial = this.serial;
    params.manufacturer = this.manufacturer;
    params.type = this.type;
    params.protocol = this.protocol;

    // TODO: move implementation into this function
    // TODO: BT server needs to stoplistening if the last device wants to stoplistening, because this is all emulated to be multi hardware
    this.service.stopListen(params, callback);

    return this;
};

/*
 * Initialize medical device plugin
 */

initialize();

module.exports = medicalDevicePlugin;