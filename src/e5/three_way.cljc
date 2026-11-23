(ns e5.three-way
  "Three-way traffic light specification (:east, :north, :south) conforming to R1-R10."
  (:require [statecharts.core :as fsm :refer [assign]]))

(def signal-order
  "Fair round-robin rotation order across signal devices."
  [:east :north :south])

(defn next-signal
  "Returns the next signal keyword in round-robin order."
  [current]
  (let [idx (.indexOf ^java.util.List signal-order current)]
    (nth signal-order (mod (inc idx) (count signal-order)))))

(defn all-red?
  "Returns true when :east, :north, and :south are all :red."
  [state]
  (every? #(= :red (get state %)) signal-order))

(defn- step-signal-colour [colour active]
  (case colour
    :red       {:colour :red-amber :active active}
    :red-amber {:colour :green     :active active}
    :green     {:colour :amber     :active active}
    :amber     {:colour :red       :active (next-signal active)}
    {:colour colour :active active}))

(defn- wind-down-colour [colour active]
  (case colour
    :green     {:colour :amber :active active}
    :amber     {:colour :red   :active (next-signal active)}
    :red-amber {:colour :red   :active active}
    {:colour colour :active active}))

(defn advance-context
  "Advances the active signal through the UK sequence while keeping other signals :red."
  [state _event]
  (let [active (:active state :east)
        colour (get state active :red)
        {:keys [colour active]} (step-signal-colour colour active)]
    (-> state
        (assoc active colour)
        (assoc :active active))))

(defn graceful-halt-context
  "Steps any active non-:red signal toward :red before halting."
  [state _event]
  (let [active (:active state :east)
        colour (get state active :red)
        {:keys [colour active]} (wind-down-colour colour active)]
    (-> state
        (assoc active colour)
        (assoc :active active))))

(defn three-way-spec
  "Returns the clj-statecharts machine definition for the three-way intersection."
  []
  (fsm/machine
   {:id      :three-way
    :initial :running
    :context {:east   :red
              :north  :red
              :south  :red
              :active :east}
    :states
    {:running
     {:on {:next {:actions (assign advance-context)}
           :halt [{:cond    (fn [state _event] (all-red? state))
                   :target  :halted}
                  {:actions (assign graceful-halt-context)}]}}
     :halted
     {:type :final}}}))

(defn create-service
  "Creates and starts a running service for the three-way traffic lights."
  []
  (let [svc (fsm/service (three-way-spec))]
    (fsm/start svc)
    svc))

(defn current-state
  "Returns the top-level state value of the service."
  [svc]
  (fsm/value svc))

(defn signals-state
  "Returns {:east <colour> :north <colour> :south <colour>} from the service state."
  [svc]
  (select-keys (fsm/state svc) signal-order))