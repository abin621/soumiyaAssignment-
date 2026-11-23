(ns e5.one-way-without-ctrl
  "One-way traffic light specification without full R1-R10 coordination control.
   Follows the UK signal sequence: :red -> :red-amber -> :green -> :amber -> :red."
  (:require [statecharts.core :as fsm]))

(def uk-cycle-states
  "Shared UK traffic light colour states and :next transitions."
  {:red       {:on {:next :red-amber}}
   :red-amber {:on {:next :green}}
   :green     {:on {:next :amber}}
   :amber     {:on {:next :red}}})

(defn one-way-without-ctrl-spec
  "Returns the clj-statecharts machine definition for an uncontrolled single signal."
  []
  (fsm/machine
   {:id      :one-way-without-ctrl
    :initial :red
    :states  uk-cycle-states}))

(defn create-service
  "Creates and starts a running service instance for the uncontrolled signal."
  []
  (let [svc (fsm/service (one-way-without-ctrl-spec))]
    (fsm/start svc)
    svc))