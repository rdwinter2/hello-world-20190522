#!/usr/bin/env perl
#use Data::Dumper;
use YAML::Tiny;

# Read schema definition file
# python example: ''.join(j.capitalize() if i>0 else j.lower() for (i,j) in enumerate(x.split()))

sub KABAB {my $s = join('-', map {uc $_} split(/(?=[A-Z])/, $_[0]));}
sub kabab {my $s = join('-', map {lc $_} split(/(?=[A-Z])/, $_[0]));}
sub snake {my $s = join('_', map {lc $_} split(/(?=[A-Z])/, $_[0]));}
sub word {my $s = join(' ', map {ucfirst $_} split(/(?=[A-Z])/, $_[0]));}
sub SNAKE {uc(snake($_[0]));}
sub camel {lcfirst $_[0];}
sub Camel {ucfirst $_[0];}

my $yaml = YAML::Tiny->new();
my %sum_types;
my %product_types;
my %scalar_types;
my $current_type = '';
my %current = {};
my $t = ''; # type name of current type being processed
my $type_flag = 'scalar';
my $indent = "  ";
my $type_start = 0;
my %sum_parents;
while (<>) {
  chomp;
  next if /^---$/;
  next if /^...$/;
  next if /^#/;
  my $leading_spaces = () = $_ =~ /\G\s/g;
  if ( ($leading_spaces == 0) && ($current_type ne '') ) {
    $sum_types{$t} = $current_type if $type_flag eq 'sum';
    $product_types{$t} = $current_type if $type_flag eq 'product';
    $scalar_types{$t} = $current_type if $type_flag eq 'scalar';
    $type_flag = 'scalar';
  }
  if ($leading_spaces == 0) {
    @_ = split(':');
    $t = @_[0];  # type name
    $yaml->[0]->{$t} = { "name" => $t };
    $yaml->[0]->{$t}{"word"} = word($t);
    $yaml->[0]->{$t}{"camel"} = camel($t);
    $yaml->[0]->{$t}{"Camel"} = Camel($t);
    $yaml->[0]->{$t}{"KABAB"} = KABAB($t);
    $yaml->[0]->{$t}{"kabab"} = kabab($t);
    $yaml->[0]->{$t}{"snake"} = snake($t);
    $yaml->[0]->{$t}{"SNAKE"} = SNAKE($t);
    $current_type = "  - name: @_[0]\n";
    $current_type .= "    word: " . word(@_[0]) . "\n";
    $current_type .= "    camel: " . camel(@_[0]) . "\n";
    $current_type .= "    Camel: " . Camel(@_[0]) . "\n";
    $current_type .= "    KABAB: " . KABAB(@_[0]) . "\n";
    $current_type .= "    kabab: " . kabab(@_[0]) . "\n";
    $current_type .= "    snake: " . snake(@_[0]) . "\n";
    $current_type .= "    SNAKE: " . SNAKE(@_[0]) . "\n";
    $type_start = 1;
    if ( $#_ > 0 ) {
      # TODO: filter out predicates, etc.
      $yaml->[0]->{$t}{"type"} = "scalar";
      my $base_type = $_[1];
      $base_type =~ s/^\s+//;
      $base_type =~ s/\s+$//;
      $yaml->[0]->{$t}{"base_type"} = $base_type;
    }
  } else {
    if ( $type_start == 1 ) {
      if (/\s+\|/) {
        $yaml->[0]->{$t}{"type"} = "sum";
        $type_flag = 'sum';
        $current_type .= "    enumerations:\n";
      } else {
        $yaml->[0]->{$t}{"type"} = "product";
        $type_flag = 'product';
        $current_type .= "    parameters:\n";
      }
      # $yaml->[0]->{$t}{"constituents"} = [];
    }
    my $r = $_;
    $r =~ s/^\s*\|{0,1}\s*//;
    @r = split(':', $r);
    $s = $r[0]; # subtype
#    %element = { "name" => $s };
    push @{$yaml->[0]->{$t}{"constituents"}}, { $s => { "name" => $s } };
    my $i = $#{$yaml->[0]->{$t}{"constituents"}}; # index position of array for new element
    $yaml->[0]->{$t}{"constituents"}[$i]{$s}{"word"} = word($s);
    $yaml->[0]->{$t}{"constituents"}[$i]{$s}{"camel"} = camel($s);
    $yaml->[0]->{$t}{"constituents"}[$i]{$s}{"Camel"} = Camel($s);
    $yaml->[0]->{$t}{"constituents"}[$i]{$s}{"KABAB"} = KABAB($s);
    $yaml->[0]->{$t}{"constituents"}[$i]{$s}{"kabab"} = kabab($s);
    $yaml->[0]->{$t}{"constituents"}[$i]{$s}{"snake"} = snake($s);
    $yaml->[0]->{$t}{"constituents"}[$i]{$s}{"SNAKE"} = SNAKE($s);
    $current_type .= "      - name: $r[0]\n";
    $current_type .= "        word: " . word($r[0]) . "\n";
    $current_type .= "        camel: " . camel($r[0]) . "\n";
    $current_type .= "        Camel: " . Camel($r[0]) . "\n";
    $current_type .= "        KABAB: " . KABAB($r[0]) . "\n";
    $current_type .= "        kabab: " . kabab($r[0]) . "\n";
    $current_type .= "        snake: " . snake($r[0]) . "\n";
    $current_type .= "        SNAKE: " . SNAKE($r[0]) . "\n";
    if ( $type_flag eq 'sum' ) {
      $sum_parents{$r[0]} = $r;
    }
    if ( $#r > 0 ) {
      $r[1] =~ s/^\s*//;
      if ( $r[1] =~ /!/ ) {
        $r[1] =~ s/!//;
        $current_type .= "        required: true\n";
        $current_type .= "        scala_type: $r[1]\n";
      } else {
        $current_type .= "        required: false\n";
        $current_type .= "        scala_type: Option[$r[1]]\n";
      }
      $current_type .= "        type: $r[1]\n";
    }
    $type_start = 0;
  }
}
if ($current_type ne '') {
  $sum_types{$t} = $current_type if $type_flag eq 'sum';
  $product_types{$t} = $current_type if $type_flag eq 'product';
  $scalar_types{$t} = $current_type if $type_flag eq 'scalar';
}
for my $t (keys %sum_parents) {
  if ( exists $product_types{$t} ) {
    my @tmp = split(/\n/, $product_types{$t});
    my $tmp = shift(@tmp);
    my $rest = join("\n", @tmp);
    $product_types{$t} = $tmp . "\n" . "    parent_sum_type: " . $sum_parents{$t} . "\n" . $rest . "\n";
  } else {
    $scalar_types{$t} = "  - name: " . $t . "\n" . "    parent_sum_type: " . $sum_parents{$t} . "\n";
  }
}
#print "---\n";
#print "sum_types:\n";
#for my $t (keys %sum_types) {
#  print "$sum_types{$t}";
#}
#print "product_types:\n";
#for my $t (keys %product_types) {
#  print "$product_types{$t}";
#}
#print "scalar_types:\n";
#for my $t (keys %scalar_types) {
#  print "$scalar_types{$t}";
#}
my $yaml_out = $yaml->write_string;
#print $yaml_out;
my $dash = 0;
foreach (split(/\n/,$yaml_out)) {
  if (/^(\s+-)\s*$/) {
    $dash_text = $1;
#    print "dash by itself ::" . $dash_text . "::\n";
    /(-)/;
    $dash = $-[1]; # position of dash
#    print "position " . $dash . "\n";
    # flag for next line processing
  } else {
    my $line = $_;
    if ($dash > 0) {
      my $spaces = substr $line, 0, ($dash+2);
      my $content = substr $line, ($dash+2);
      $line = $dash_text . " " . $content;
    }
    print $line . "\n";
    $dash = 0;
  }
}

#$yaml->write( 'data.yml' );