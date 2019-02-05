Schema Generation
=================

## Description

## Documentation

## Requirements

## Dependencies

None

## Setup

## Testing

## Role Variables

| Name | Default Value | Description |
|------|---------------|-------------|
|

## Example Playbook

```{.yml}
- name: Exercise the role schema-gen
  hosts: all
  become: false
  roles:
    - { role: schema-gen }
```

## Configuration

## Road Map

### Planned Additions

### Current Issues

### Changelog

## Discussion

## Transcluded Content

## License

MIT

## Author Information

| Author                | E-mail               |
|-----------------------|----------------------|
| Robert D. Winter, 2nd |  rdwinter2@gmail.com |

## References

* https://www.arctiq.ca/our-blog/2017/2/16/ansible-jinja-warrior-loop-variable-scope/


First add the following line to your ansible.cfg:

[defaults]
jinja2_extensions = jinja2.ext.do,jinja2.ext.i18n
Then modify your varloop.j2 file like this:

{% for colour in colours %}
  Colour number {{ loop.index }} is {{ colour.name }}.
{% set colour_count = 0 %}
{% for person in people if person.fav_colour == colour.name %}
{% set colour_count = colour_count + 1 %}
{% do colour.update({'people_count':colour_count}) %}
{% endfor %}
  Currently {{ colour.people_count }} people call {{ colour.name }} their favourite.
  And the following are examples of things that are {{ colour.name }}:
{% for item in colour.things %}
    - {{ item }}
{% endfor %}

{% endfor %}