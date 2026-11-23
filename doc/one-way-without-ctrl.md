# One-Way Traffic Light (Without Control)

```mermaid
stateDiagram-v2
    [*] --> red
    red --> red_amber : next
    red_amber --> green : next
    green --> amber : next
    amber --> red : next