(ns e5.one-way
  "One-way traffic light specification conforming to R1-R10.
   Starts at :red, advances on :next through the UK cycle, and supports
   graceful transition to :red and :halt."
  (:require [e5.one-way-without-ctrl :as base]
            [statecharts.core :as fsm]))

(defn one-way-spec
  "Returns the clj-statecharts machine definition for a single controlled signal.
   - :next advances the UK signal cycle from :red.
   - :halt transitions gracefully back to :red first if currently green/amber/red-amber,
     and :halt from :red moves to :halt."
  []
  (fsm/machine
   {:id      :one-way
    :initial :red
    :states
    {:red       {:on {:next :red-amber
                      :halt :halt}}
     :red-amber {:on {:next :green
                      :halt :red}}
     :green     {:on {:next :amber
                      :halt :amber}}
     :amber     {:on {:next :red
                      :halt :red}}
     :halt      {:type :final}}}))

(defn create-service
  "Creates and starts a running service instance for the controlled one-way signal."
  []
  (let [svc (fsm/service (one-way-spec))]
    (fsm/start svc)
    svc))

(defn step!
  "Sends an event (default :next) to a service and returns the current state."
  ([svc] (step! svc :next))
  ([svc event]
   (fsm/send svc event)
   @(:state svc)))