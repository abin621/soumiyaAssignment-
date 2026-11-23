(ns e5.three-way-test
  (:require [clojure.test :refer [deftest is testing]]
            [e5.three-way :as sut]
            [statecharts.core :as fsm]))

(defn- active-non-red-signals [sigs]
  (->> sigs
       (filter (fn [[_ colour]] (not= :red colour)))
       (map key)
       set))

(deftest initial-all-red-and-mutual-exclusion
  (testing "Starts all-red across :east, :north, :south and never overlaps :green or :amber"
    (let [svc (sut/create-service)]
      (is (= {:east :red :north :red :south :red} (sut/signals-state svc)))
      (dotimes [_ 20]
        (fsm/send svc :next)
        (let [sigs    (sut/signals-state svc)
              non-red (active-non-red-signals sigs)]
          (is (<= (count non-red) 1)
              (str "Expected at most one non-red signal, got: " sigs)))))))

(deftest fair-round-robin-liveness
  (testing "Every signal (:east, :north, :south) eventually displays :green in fair order"
    (let [svc    (sut/create-service)
          greens (atom [])]
      (dotimes [_ 12]
        (fsm/send svc :next)
        (let [sigs (sut/signals-state svc)]
          (doseq [[k colour] sigs]
            (when (= :green colour)
              (swap! greens conj k)))))
      (is (= [:east :north :south] @greens)))))

(deftest graceful-halt-to-all-red
  (testing "Gracefully returns signals to all-red before reaching :halted"
    (let [svc (sut/create-service)]
      (fsm/send svc :next)
      (fsm/send svc :next)
      (fsm/send svc :halt)
      (is (= :running (sut/current-state svc)))
      (is (= :amber (:east (sut/signals-state svc))))
      (fsm/send svc :halt)
      (is (= :running (sut/current-state svc)))
      (is (= {:east :red :north :red :south :red} (sut/signals-state svc)))
      (fsm/send svc :halt)
      (is (= :halted (sut/current-state svc))))))