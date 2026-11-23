(ns e5.one-way-test
  (:require [clojure.test :refer [deftest is testing]]
            [e5.one-way :as sut]))

(deftest controlled-cycle-and-halt
  (testing "Starts at :red, cycles through UK sequence, and transitions to :halt"
    (let [svc (sut/create-service)]
      (is (= :red (sut/current-state svc)))
      (is (= :red-amber (sut/step! svc :next)))
      (is (= :green (sut/step! svc :next)))
      (is (= :amber (sut/step! svc :next)))
      (is (= :red (sut/step! svc :next)))
      (is (= :halt (sut/step! svc :halt)))))

  (testing "Graceful wind-down on :halt from :green -> :amber -> :red -> :halt"
    (let [svc (sut/create-service)]
      (sut/step! svc :next)
      (sut/step! svc :next)
      (is (= :amber (sut/step! svc :halt)))
      (is (= :red (sut/step! svc :halt)))
      (is (= :halt (sut/step! svc :halt))))))