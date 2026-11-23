#### `doc/three-way.md`
```markdown
# Three-Way Traffic Light (`:east`, `:north`, `:south`, R1-R10)

```mermaid
stateDiagram-v2
    [*] --> running

    state running {
        [*] --> all_red
        all_red --> active_cycle : next
        active_cycle --> active_cycle : next (red-amber -> green -> amber -> red)
        active_cycle --> all_red : next (amber -> red, pass :active turn)
    }

    running --> running : halt [not all-red: wind down active signal]
    running --> halted : halt [all-red]
    halted --> [*]