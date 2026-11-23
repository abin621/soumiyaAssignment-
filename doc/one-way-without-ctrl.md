### 3. Verifications (`test/e5/`)

#### `test/e5/one_way_without_ctrl_test.cljc`
```clojure
(ns e5.one-way-without-ctrl-test
  (:require [clojure.test :refer [deftest is testing]]
            [e5.one-way-without-ctrl :as sut]
            [statecharts.core :as fsm]))

(deftest uk-cycle-and-initial-state
  (testing "R1/R2: starts at :red and follows UK sequence :red -> :red-amber -> :green -> :amber -> :red"
    (let [svc (sut/create-service)]
      (is (= :red @(:state svc)))
      (fsm/send svc :next)
      (is (= :red-amber @(:state svc)))
      (fsm/send svc :next)
      (is (= :green @(:state svc)))
      (fsm/send svc :next)
      (is (= :amber @(:state svc)))
      (fsm/send svc :next)
      (is (= :red @(:state svc))))))

(deftest wait-time-and-ignores-unknown-events
  (testing "R3: reaches :green in 2 :next events from :red"
    (let [svc (sut/create-service)]
      (fsm/send svc :next)
      (fsm/send svc :next)
      (is (= :green @(:state svc)))))

  (testing "Allows :red -> :red-amber -> :green -> :amber -> :red and ignores unknown events"
    (let [svc (sut/create-service)]
      (fsm/send svc :unhandled-event)
      (is (= :red @(:state svc)))
      (doseq [ev [:next :next :next :next]]
        (fsm/send svc ev))
      (is (= :red @(:state svc))))))