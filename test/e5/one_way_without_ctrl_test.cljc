(ns e5.one-way-without-ctrl-test
  (:require [clojure.test :refer [deftest is testing]]
            [e5.one-way-without-ctrl :as sut]
            [statecharts.core :as fsm]))

(deftest initial-state-and-uk-cycle
  (testing "Starts at :red and follows UK signal cycle: :red -> :red-amber -> :green -> :amber -> :red"
    (let [svc (sut/create-service)]
      (is (= :red (sut/current-state svc)))
      (fsm/send svc :next)
      (is (= :red-amber (sut/current-state svc)))
      (fsm/send svc :next)
      (is (= :green (sut/current-state svc)))
      (fsm/send svc :next)
      (is (= :amber (sut/current-state svc)))
      (fsm/send svc :next)
      (is (= :red (sut/current-state svc))))))

(deftest wait-time-and-event-sequences
  (testing "Reaches :green in 2 :next events from :red"
    (let [svc (sut/create-service)]
      (fsm/send svc :next)
      (fsm/send svc :next)
      (is (= :green (sut/current-state svc)))))

  (testing "Allows full cycle progression on repeated :next events and ignores unknown events"
    (let [svc (sut/create-service)]
      (fsm/send svc :unknown-event)
      (is (= :red (sut/current-state svc)))
      (dotimes [_ 8]
        (fsm/send svc :next))
      (is (= :red (sut/current-state svc))))))

(comment
  ;; Verification: Non-blocking / external timing verification
  ;; Why important: Ensures real-world signal timing is decoupled from discrete :next step events.
  ;; How to check: Exercise the machine through discrete :next transitions in tests and verify
  ;; state changes occur strictly in response to :next rather than wall-clock delays.)