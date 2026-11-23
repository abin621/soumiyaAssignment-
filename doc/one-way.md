#### `doc/one-way.md`
```markdown
# One-Way Traffic Light (Controlled, R1-R10)

```mermaid
stateDiagram-v2
    [*] --> red
    red --> red_amber : next
    red_amber --> green : next
    green --> amber : next
    amber --> red : next

    red --> halted : halt
    red_amber --> red : halt
    green --> amber : halt
    amber --> red : halt
    halted --> [*]