#!/usr/bin/env perl
use YAML::Tiny;

my %yaml;
$s = 'wibble';
my $yaml->{$s} = "wobble";

$yaml->{'extra'} = [ 'foo' => { 'name' => 'bob' }, 'bar', 'baz' ];

my $out = Dump($yaml);
print $out;